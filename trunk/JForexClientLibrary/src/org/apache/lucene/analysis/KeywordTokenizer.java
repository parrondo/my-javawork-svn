/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*    */ import org.apache.lucene.util.AttributeSource;
/*    */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*    */ 
/*    */ public final class KeywordTokenizer extends Tokenizer
/*    */ {
/*    */   private static final int DEFAULT_BUFFER_SIZE = 256;
/* 34 */   private boolean done = false;
/*    */   private int finalOffset;
/* 36 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/* 37 */   private OffsetAttribute offsetAtt = (OffsetAttribute)addAttribute(OffsetAttribute.class);
/*    */ 
/*    */   public KeywordTokenizer(Reader input) {
/* 40 */     this(input, 256);
/*    */   }
/*    */ 
/*    */   public KeywordTokenizer(Reader input, int bufferSize) {
/* 44 */     super(input);
/* 45 */     this.termAtt.resizeBuffer(bufferSize);
/*    */   }
/*    */ 
/*    */   public KeywordTokenizer(AttributeSource source, Reader input, int bufferSize) {
/* 49 */     super(source, input);
/* 50 */     this.termAtt.resizeBuffer(bufferSize);
/*    */   }
/*    */ 
/*    */   public KeywordTokenizer(AttributeSource.AttributeFactory factory, Reader input, int bufferSize) {
/* 54 */     super(factory, input);
/* 55 */     this.termAtt.resizeBuffer(bufferSize);
/*    */   }
/*    */ 
/*    */   public final boolean incrementToken() throws IOException
/*    */   {
/* 60 */     if (!this.done) {
/* 61 */       clearAttributes();
/* 62 */       this.done = true;
/* 63 */       int upto = 0;
/* 64 */       char[] buffer = this.termAtt.buffer();
/*    */       while (true) {
/* 66 */         int length = this.input.read(buffer, upto, buffer.length - upto);
/* 67 */         if (length == -1) break;
/* 68 */         upto += length;
/* 69 */         if (upto == buffer.length)
/* 70 */           buffer = this.termAtt.resizeBuffer(1 + buffer.length);
/*    */       }
/* 72 */       this.termAtt.setLength(upto);
/* 73 */       this.finalOffset = correctOffset(upto);
/* 74 */       this.offsetAtt.setOffset(correctOffset(0), this.finalOffset);
/* 75 */       return true;
/*    */     }
/* 77 */     return false;
/*    */   }
/*    */ 
/*    */   public final void end()
/*    */   {
/* 83 */     this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
/*    */   }
/*    */ 
/*    */   public void reset(Reader input) throws IOException
/*    */   {
/* 88 */     super.reset(input);
/* 89 */     this.done = false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.KeywordTokenizer
 * JD-Core Version:    0.6.0
 */