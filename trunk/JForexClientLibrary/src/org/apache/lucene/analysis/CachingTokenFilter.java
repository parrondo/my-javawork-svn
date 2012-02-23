/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import org.apache.lucene.util.AttributeSource.State;
/*    */ 
/*    */ public final class CachingTokenFilter extends TokenFilter
/*    */ {
/* 37 */   private List<AttributeSource.State> cache = null;
/* 38 */   private Iterator<AttributeSource.State> iterator = null;
/*    */   private AttributeSource.State finalState;
/*    */ 
/*    */   public CachingTokenFilter(TokenStream input)
/*    */   {
/* 42 */     super(input);
/*    */   }
/*    */ 
/*    */   public final boolean incrementToken() throws IOException
/*    */   {
/* 47 */     if (this.cache == null)
/*    */     {
/* 49 */       this.cache = new LinkedList();
/* 50 */       fillCache();
/* 51 */       this.iterator = this.cache.iterator();
/*    */     }
/*    */ 
/* 54 */     if (!this.iterator.hasNext())
/*    */     {
/* 56 */       return false;
/*    */     }
/*    */ 
/* 59 */     restoreState((AttributeSource.State)this.iterator.next());
/* 60 */     return true;
/*    */   }
/*    */ 
/*    */   public final void end() throws IOException
/*    */   {
/* 65 */     if (this.finalState != null)
/* 66 */       restoreState(this.finalState);
/*    */   }
/*    */ 
/*    */   public void reset()
/*    */     throws IOException
/*    */   {
/* 72 */     if (this.cache != null)
/* 73 */       this.iterator = this.cache.iterator();
/*    */   }
/*    */ 
/*    */   private void fillCache() throws IOException
/*    */   {
/* 78 */     while (this.input.incrementToken()) {
/* 79 */       this.cache.add(captureState());
/*    */     }
/*    */ 
/* 82 */     this.input.end();
/* 83 */     this.finalState = captureState();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.CachingTokenFilter
 * JD-Core Version:    0.6.0
 */