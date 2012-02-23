/*     */ package org.apache.lucene.analysis.standard;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.analysis.Tokenizer;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class ClassicTokenizer extends Tokenizer
/*     */ {
/*     */   private StandardTokenizerInterface scanner;
/*     */   public static final int ALPHANUM = 0;
/*     */   public static final int APOSTROPHE = 1;
/*     */   public static final int ACRONYM = 2;
/*     */   public static final int COMPANY = 3;
/*     */   public static final int EMAIL = 4;
/*     */   public static final int HOST = 5;
/*     */   public static final int NUM = 6;
/*     */   public static final int CJ = 7;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int ACRONYM_DEP = 8;
/*  81 */   public static final String[] TOKEN_TYPES = { "<ALPHANUM>", "<APOSTROPHE>", "<ACRONYM>", "<COMPANY>", "<EMAIL>", "<HOST>", "<NUM>", "<CJ>", "<ACRONYM_DEP>" };
/*     */   private boolean replaceInvalidAcronym;
/*  95 */   private int maxTokenLength = 255;
/*     */ 
/* 150 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/* 151 */   private final OffsetAttribute offsetAtt = (OffsetAttribute)addAttribute(OffsetAttribute.class);
/* 152 */   private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)addAttribute(PositionIncrementAttribute.class);
/* 153 */   private final TypeAttribute typeAtt = (TypeAttribute)addAttribute(TypeAttribute.class);
/*     */ 
/*     */   public void setMaxTokenLength(int length)
/*     */   {
/* 100 */     this.maxTokenLength = length;
/*     */   }
/*     */ 
/*     */   public int getMaxTokenLength()
/*     */   {
/* 105 */     return this.maxTokenLength;
/*     */   }
/*     */ 
/*     */   public ClassicTokenizer(Version matchVersion, Reader input)
/*     */   {
/* 118 */     init(input, matchVersion);
/*     */   }
/*     */ 
/*     */   public ClassicTokenizer(Version matchVersion, AttributeSource source, Reader input)
/*     */   {
/* 125 */     super(source);
/* 126 */     init(input, matchVersion);
/*     */   }
/*     */ 
/*     */   public ClassicTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input)
/*     */   {
/* 133 */     super(factory);
/* 134 */     init(input, matchVersion);
/*     */   }
/*     */ 
/*     */   private final void init(Reader input, Version matchVersion) {
/* 138 */     this.scanner = new ClassicTokenizerImpl(input);
/*     */ 
/* 140 */     if (matchVersion.onOrAfter(Version.LUCENE_24))
/* 141 */       this.replaceInvalidAcronym = true;
/*     */     else {
/* 143 */       this.replaceInvalidAcronym = false;
/*     */     }
/* 145 */     this.input = input;
/*     */   }
/*     */ 
/*     */   public final boolean incrementToken()
/*     */     throws IOException
/*     */   {
/* 162 */     clearAttributes();
/* 163 */     int posIncr = 1;
/*     */     while (true)
/*     */     {
/* 166 */       int tokenType = this.scanner.getNextToken();
/*     */ 
/* 168 */       if (tokenType == -1) {
/* 169 */         return false;
/*     */       }
/*     */ 
/* 172 */       if (this.scanner.yylength() <= this.maxTokenLength) {
/* 173 */         this.posIncrAtt.setPositionIncrement(posIncr);
/* 174 */         this.scanner.getText(this.termAtt);
/* 175 */         int start = this.scanner.yychar();
/* 176 */         this.offsetAtt.setOffset(correctOffset(start), correctOffset(start + this.termAtt.length()));
/*     */ 
/* 180 */         if (tokenType == 8) {
/* 181 */           if (this.replaceInvalidAcronym) {
/* 182 */             this.typeAtt.setType(TOKEN_TYPES[5]);
/* 183 */             this.termAtt.setLength(this.termAtt.length() - 1);
/*     */           } else {
/* 185 */             this.typeAtt.setType(TOKEN_TYPES[2]);
/*     */           }
/*     */         }
/* 188 */         else this.typeAtt.setType(TOKEN_TYPES[tokenType]);
/*     */ 
/* 190 */         return true;
/*     */       }
/*     */ 
/* 194 */       posIncr++;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void end()
/*     */   {
/* 201 */     int finalOffset = correctOffset(this.scanner.yychar() + this.scanner.yylength());
/* 202 */     this.offsetAtt.setOffset(finalOffset, finalOffset);
/*     */   }
/*     */ 
/*     */   public void reset(Reader reader) throws IOException
/*     */   {
/* 207 */     super.reset(reader);
/* 208 */     this.scanner.yyreset(reader);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean isReplaceInvalidAcronym()
/*     */   {
/* 220 */     return this.replaceInvalidAcronym;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void setReplaceInvalidAcronym(boolean replaceInvalidAcronym)
/*     */   {
/* 232 */     this.replaceInvalidAcronym = replaceInvalidAcronym;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.ClassicTokenizer
 * JD-Core Version:    0.6.0
 */