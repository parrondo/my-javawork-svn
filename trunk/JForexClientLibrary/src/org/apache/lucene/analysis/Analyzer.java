/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import org.apache.lucene.document.Fieldable;
/*     */ import org.apache.lucene.store.AlreadyClosedException;
/*     */ import org.apache.lucene.util.CloseableThreadLocal;
/*     */ 
/*     */ public abstract class Analyzer
/*     */   implements Closeable
/*     */ {
/*  81 */   private CloseableThreadLocal<Object> tokenStreams = new CloseableThreadLocal();
/*     */ 
/*     */   protected Analyzer()
/*     */   {
/*  45 */     assert (assertFinal());
/*     */   }
/*     */ 
/*     */   private boolean assertFinal() {
/*     */     try {
/*  50 */       Class clazz = getClass();
/*     */ 
/*  57 */       if ((!$assertionsDisabled) && (!clazz.isAnonymousClass()) && ((clazz.getModifiers() & 0x12) == 0)) if (Modifier.isFinal(clazz.getMethod("tokenStream", new Class[] { String.class, Reader.class }).getModifiers())) { if (Modifier.isFinal(clazz.getMethod("reusableTokenStream", new Class[] { String.class, Reader.class }).getModifiers())); } else throw new AssertionError("Analyzer implementation classes or at least their tokenStream() and reusableTokenStream() implementations must be final");
/*  58 */       return true; } catch (NoSuchMethodException nsme) {
/*     */     }
/*  60 */     return false;
/*     */   }
/*     */ 
/*     */   public abstract TokenStream tokenStream(String paramString, Reader paramReader);
/*     */ 
/*     */   public TokenStream reusableTokenStream(String fieldName, Reader reader)
/*     */     throws IOException
/*     */   {
/*  78 */     return tokenStream(fieldName, reader);
/*     */   }
/*     */ 
/*     */   protected Object getPreviousTokenStream()
/*     */   {
/*     */     try
/*     */     {
/*  88 */       return this.tokenStreams.get();
/*     */     } catch (NullPointerException npe) {
/*  90 */       if (this.tokenStreams == null)
/*  91 */         throw new AlreadyClosedException("this Analyzer is closed");
/*     */     }
/*  93 */     throw npe;
/*     */   }
/*     */ 
/*     */   protected void setPreviousTokenStream(Object obj)
/*     */   {
/*     */     try
/*     */     {
/* 103 */       this.tokenStreams.set(obj);
/*     */     } catch (NullPointerException npe) {
/* 105 */       if (this.tokenStreams == null) {
/* 106 */         throw new AlreadyClosedException("this Analyzer is closed");
/*     */       }
/* 108 */       throw npe;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getPositionIncrementGap(String fieldName)
/*     */   {
/* 127 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getOffsetGap(Fieldable field)
/*     */   {
/* 142 */     if (field.isTokenized()) {
/* 143 */       return 1;
/*     */     }
/* 145 */     return 0;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 150 */     this.tokenStreams.close();
/* 151 */     this.tokenStreams = null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.Analyzer
 * JD-Core Version:    0.6.0
 */