/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import org.apache.lucene.util.AttributeSource;
/*    */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*    */ 
/*    */ public abstract class Tokenizer extends TokenStream
/*    */ {
/*    */   protected Reader input;
/*    */ 
/*    */   protected Tokenizer()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected Tokenizer(Reader input)
/*    */   {
/* 42 */     this.input = CharReader.get(input);
/*    */   }
/*    */ 
/*    */   protected Tokenizer(AttributeSource.AttributeFactory factory)
/*    */   {
/* 47 */     super(factory);
/*    */   }
/*    */ 
/*    */   protected Tokenizer(AttributeSource.AttributeFactory factory, Reader input)
/*    */   {
/* 52 */     super(factory);
/* 53 */     this.input = CharReader.get(input);
/*    */   }
/*    */ 
/*    */   protected Tokenizer(AttributeSource source)
/*    */   {
/* 58 */     super(source);
/*    */   }
/*    */ 
/*    */   protected Tokenizer(AttributeSource source, Reader input)
/*    */   {
/* 63 */     super(source);
/* 64 */     this.input = CharReader.get(input);
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 70 */     if (this.input != null) {
/* 71 */       this.input.close();
/*    */ 
/* 74 */       this.input = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   protected final int correctOffset(int currentOff)
/*    */   {
/* 85 */     return (this.input instanceof CharStream) ? ((CharStream)this.input).correctOffset(currentOff) : currentOff;
/*    */   }
/*    */ 
/*    */   public void reset(Reader input)
/*    */     throws IOException
/*    */   {
/* 92 */     this.input = input;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.Tokenizer
 * JD-Core Version:    0.6.0
 */