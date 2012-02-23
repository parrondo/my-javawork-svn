/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.util.AttributeImpl;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.State;
/*     */ 
/*     */ public final class TeeSinkTokenFilter extends TokenFilter
/*     */ {
/*  76 */   private final List<WeakReference<SinkTokenStream>> sinks = new LinkedList();
/*     */ 
/* 238 */   private static final SinkFilter ACCEPT_ALL_FILTER = new SinkFilter()
/*     */   {
/*     */     public boolean accept(AttributeSource source) {
/* 241 */       return true;
/*     */     }
/* 238 */   };
/*     */ 
/*     */   public TeeSinkTokenFilter(TokenStream input)
/*     */   {
/*  82 */     super(input);
/*     */   }
/*     */ 
/*     */   public SinkTokenStream newSinkTokenStream()
/*     */   {
/*  89 */     return newSinkTokenStream(ACCEPT_ALL_FILTER);
/*     */   }
/*     */ 
/*     */   public SinkTokenStream newSinkTokenStream(SinkFilter filter)
/*     */   {
/*  98 */     SinkTokenStream sink = new SinkTokenStream(cloneAttributes(), filter, null);
/*  99 */     this.sinks.add(new WeakReference(sink));
/* 100 */     return sink;
/*     */   }
/*     */ 
/*     */   public void addSinkTokenStream(SinkTokenStream sink)
/*     */   {
/* 110 */     if (!getAttributeFactory().equals(sink.getAttributeFactory())) {
/* 111 */       throw new IllegalArgumentException("The supplied sink is not compatible to this tee");
/*     */     }
/*     */ 
/* 114 */     for (Iterator it = cloneAttributes().getAttributeImplsIterator(); it.hasNext(); ) {
/* 115 */       sink.addAttributeImpl((AttributeImpl)it.next());
/*     */     }
/* 117 */     this.sinks.add(new WeakReference(sink));
/*     */   }
/*     */ 
/*     */   public void consumeAllTokens() throws IOException
/*     */   {
/* 127 */     while (incrementToken());
/*     */   }
/*     */ 
/*     */   public boolean incrementToken() throws IOException
/*     */   {
/* 132 */     if (this.input.incrementToken())
/*     */     {
/* 134 */       AttributeSource.State state = null;
/* 135 */       for (WeakReference ref : this.sinks) {
/* 136 */         SinkTokenStream sink = (SinkTokenStream)ref.get();
/* 137 */         if ((sink != null) && 
/* 138 */           (sink.accept(this))) {
/* 139 */           if (state == null) {
/* 140 */             state = captureState();
/*     */           }
/* 142 */           sink.addState(state);
/*     */         }
/*     */       }
/*     */ 
/* 146 */       return true;
/*     */     }
/*     */ 
/* 149 */     return false;
/*     */   }
/*     */ 
/*     */   public final void end() throws IOException
/*     */   {
/* 154 */     super.end();
/* 155 */     AttributeSource.State finalState = captureState();
/* 156 */     for (WeakReference ref : this.sinks) {
/* 157 */       SinkTokenStream sink = (SinkTokenStream)ref.get();
/* 158 */       if (sink != null)
/* 159 */         sink.setFinalState(finalState); 
/*     */     }
/*     */   }
/*     */   public static final class SinkTokenStream extends TokenStream {
/* 184 */     private final List<AttributeSource.State> cachedStates = new LinkedList();
/*     */     private AttributeSource.State finalState;
/* 186 */     private Iterator<AttributeSource.State> it = null;
/*     */     private TeeSinkTokenFilter.SinkFilter filter;
/*     */ 
/*     */     private SinkTokenStream(AttributeSource source, TeeSinkTokenFilter.SinkFilter filter) {
/* 190 */       super();
/* 191 */       this.filter = filter;
/*     */     }
/*     */ 
/*     */     private boolean accept(AttributeSource source) {
/* 195 */       return this.filter.accept(source);
/*     */     }
/*     */ 
/*     */     private void addState(AttributeSource.State state) {
/* 199 */       if (this.it != null) {
/* 200 */         throw new IllegalStateException("The tee must be consumed before sinks are consumed.");
/*     */       }
/* 202 */       this.cachedStates.add(state);
/*     */     }
/*     */ 
/*     */     private void setFinalState(AttributeSource.State finalState) {
/* 206 */       this.finalState = finalState;
/*     */     }
/*     */ 
/*     */     public final boolean incrementToken()
/*     */       throws IOException
/*     */     {
/* 212 */       if (this.it == null) {
/* 213 */         this.it = this.cachedStates.iterator();
/*     */       }
/*     */ 
/* 216 */       if (!this.it.hasNext()) {
/* 217 */         return false;
/*     */       }
/*     */ 
/* 220 */       AttributeSource.State state = (AttributeSource.State)this.it.next();
/* 221 */       restoreState(state);
/* 222 */       return true;
/*     */     }
/*     */ 
/*     */     public final void end() throws IOException
/*     */     {
/* 227 */       if (this.finalState != null)
/* 228 */         restoreState(this.finalState);
/*     */     }
/*     */ 
/*     */     public final void reset()
/*     */     {
/* 234 */       this.it = this.cachedStates.iterator();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class SinkFilter
/*     */   {
/*     */     public abstract boolean accept(AttributeSource paramAttributeSource);
/*     */ 
/*     */     public void reset()
/*     */       throws IOException
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.TeeSinkTokenFilter
 * JD-Core Version:    0.6.0
 */