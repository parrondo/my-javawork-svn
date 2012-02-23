/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ 
/*     */ public abstract class TokenStream extends AttributeSource
/*     */   implements Closeable
/*     */ {
/*     */   protected TokenStream()
/*     */   {
/*  92 */     assert (assertFinal());
/*     */   }
/*     */ 
/*     */   protected TokenStream(AttributeSource input)
/*     */   {
/*  99 */     super(input);
/* 100 */     assert (assertFinal());
/*     */   }
/*     */ 
/*     */   protected TokenStream(AttributeSource.AttributeFactory factory)
/*     */   {
/* 107 */     super(factory);
/* 108 */     assert (assertFinal());
/*     */   }
/*     */ 
/*     */   private boolean assertFinal() {
/*     */     try {
/* 113 */       Class clazz = getClass();
/*     */ 
/* 117 */       assert ((clazz.isAnonymousClass()) || ((clazz.getModifiers() & 0x12) != 0) || (Modifier.isFinal(clazz.getMethod("incrementToken", new Class[0]).getModifiers()))) : "TokenStream implementation classes or at least their incrementToken() implementation must be final";
/* 118 */       return true; } catch (NoSuchMethodException nsme) {
/*     */     }
/* 120 */     return false;
/*     */   }
/*     */ 
/*     */   public abstract boolean incrementToken()
/*     */     throws IOException;
/*     */ 
/*     */   public void end()
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.TokenStream
 * JD-Core Version:    0.6.0
 */