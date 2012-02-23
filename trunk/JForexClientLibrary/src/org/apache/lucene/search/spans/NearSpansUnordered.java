/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ 
/*     */ public class NearSpansUnordered extends Spans
/*     */ {
/*     */   private SpanNearQuery query;
/*  39 */   private List<SpansCell> ordered = new ArrayList();
/*     */   private Spans[] subSpans;
/*     */   private int slop;
/*     */   private SpansCell first;
/*     */   private SpansCell last;
/*     */   private int totalLength;
/*     */   private CellQueue queue;
/*     */   private SpansCell max;
/*  51 */   private boolean more = true;
/*  52 */   private boolean firstTime = true;
/*     */ 
/*     */   public NearSpansUnordered(SpanNearQuery query, IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 136 */     this.query = query;
/* 137 */     this.slop = query.getSlop();
/*     */ 
/* 139 */     SpanQuery[] clauses = query.getClauses();
/* 140 */     this.queue = new CellQueue(clauses.length);
/* 141 */     this.subSpans = new Spans[clauses.length];
/* 142 */     for (int i = 0; i < clauses.length; i++) {
/* 143 */       SpansCell cell = new SpansCell(clauses[i].getSpans(reader), i);
/*     */ 
/* 145 */       this.ordered.add(cell);
/* 146 */       this.subSpans[i] = SpansCell.access$300(cell);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Spans[] getSubSpans() {
/* 150 */     return this.subSpans;
/*     */   }
/*     */ 
/*     */   public boolean next() throws IOException {
/* 154 */     if (this.firstTime) {
/* 155 */       initList(true);
/* 156 */       listToQueue();
/* 157 */       this.firstTime = false;
/* 158 */     } else if (this.more) {
/* 159 */       if (min().next())
/* 160 */         this.queue.updateTop();
/*     */       else {
/* 162 */         this.more = false;
/*     */       }
/*     */     }
/*     */ 
/* 166 */     while (this.more)
/*     */     {
/* 168 */       boolean queueStale = false;
/*     */ 
/* 170 */       if (min().doc() != this.max.doc()) {
/* 171 */         queueToList();
/* 172 */         queueStale = true;
/*     */       }
/*     */ 
/* 177 */       while ((this.more) && (this.first.doc() < this.last.doc())) {
/* 178 */         this.more = this.first.skipTo(this.last.doc());
/* 179 */         firstToLast();
/* 180 */         queueStale = true;
/*     */       }
/*     */ 
/* 183 */       if (!this.more) return false;
/*     */ 
/* 187 */       if (queueStale) {
/* 188 */         listToQueue();
/* 189 */         queueStale = false;
/*     */       }
/*     */ 
/* 192 */       if (atMatch()) {
/* 193 */         return true;
/*     */       }
/*     */ 
/* 196 */       this.more = min().next();
/* 197 */       if (this.more) {
/* 198 */         this.queue.updateTop();
/*     */       }
/*     */     }
/* 201 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean skipTo(int target) throws IOException
/*     */   {
/* 206 */     if (this.firstTime) {
/* 207 */       initList(false);
/* 208 */       for (SpansCell cell = this.first; (this.more) && (cell != null); cell = cell.next) {
/* 209 */         this.more = cell.skipTo(target);
/*     */       }
/* 211 */       if (this.more) {
/* 212 */         listToQueue();
/*     */       }
/* 214 */       this.firstTime = false;
/*     */     } else {
/* 216 */       while ((this.more) && (min().doc() < target)) {
/* 217 */         if (min().skipTo(target)) {
/* 218 */           this.queue.updateTop(); continue;
/*     */         }
/* 220 */         this.more = false;
/*     */       }
/*     */     }
/*     */ 
/* 224 */     return (this.more) && ((atMatch()) || (next()));
/*     */   }
/*     */   private SpansCell min() {
/* 227 */     return (SpansCell)this.queue.top();
/*     */   }
/*     */   public int doc() {
/* 230 */     return min().doc();
/*     */   }
/* 232 */   public int start() { return min().start(); } 
/*     */   public int end() {
/* 234 */     return this.max.end();
/*     */   }
/*     */ 
/*     */   public Collection<byte[]> getPayload()
/*     */     throws IOException
/*     */   {
/* 244 */     Set matchPayload = new HashSet();
/* 245 */     for (SpansCell cell = this.first; cell != null; cell = cell.next) {
/* 246 */       if (cell.isPayloadAvailable()) {
/* 247 */         matchPayload.addAll(cell.getPayload());
/*     */       }
/*     */     }
/* 250 */     return matchPayload;
/*     */   }
/*     */ 
/*     */   public boolean isPayloadAvailable()
/*     */   {
/* 256 */     SpansCell pointer = min();
/* 257 */     while (pointer != null) {
/* 258 */       if (pointer.isPayloadAvailable()) {
/* 259 */         return true;
/*     */       }
/* 261 */       pointer = pointer.next;
/*     */     }
/*     */ 
/* 264 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 269 */     return getClass().getName() + "(" + this.query.toString() + ")@" + (this.more ? doc() + ":" + start() + "-" + end() : this.firstTime ? "START" : "END");
/*     */   }
/*     */ 
/*     */   private void initList(boolean next) throws IOException
/*     */   {
/* 274 */     for (int i = 0; (this.more) && (i < this.ordered.size()); i++) {
/* 275 */       SpansCell cell = (SpansCell)this.ordered.get(i);
/* 276 */       if (next)
/* 277 */         this.more = cell.next();
/* 278 */       if (this.more)
/* 279 */         addToList(cell);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addToList(SpansCell cell) throws IOException
/*     */   {
/* 285 */     if (this.last != null)
/* 286 */       SpansCell.access$402(this.last, cell);
/*     */     else
/* 288 */       this.first = cell;
/* 289 */     this.last = cell;
/* 290 */     SpansCell.access$402(cell, null);
/*     */   }
/*     */ 
/*     */   private void firstToLast() {
/* 294 */     SpansCell.access$402(this.last, this.first);
/* 295 */     this.last = this.first;
/* 296 */     this.first = this.first.next;
/* 297 */     SpansCell.access$402(this.last, null);
/*     */   }
/*     */ 
/*     */   private void queueToList() throws IOException {
/* 301 */     this.last = (this.first = null);
/* 302 */     while (this.queue.top() != null)
/* 303 */       addToList((SpansCell)this.queue.pop());
/*     */   }
/*     */ 
/*     */   private void listToQueue()
/*     */   {
/* 308 */     this.queue.clear();
/* 309 */     for (SpansCell cell = this.first; cell != null; cell = cell.next)
/* 310 */       this.queue.add(cell);
/*     */   }
/*     */ 
/*     */   private boolean atMatch()
/*     */   {
/* 315 */     return (min().doc() == this.max.doc()) && (this.max.end() - min().start() - this.totalLength <= this.slop);
/*     */   }
/*     */ 
/*     */   private class SpansCell extends Spans
/*     */   {
/*     */     private Spans spans;
/*     */     private SpansCell next;
/*  74 */     private int length = -1;
/*     */     private int index;
/*     */ 
/*     */     public SpansCell(Spans spans, int index)
/*     */     {
/*  78 */       this.spans = spans;
/*  79 */       this.index = index;
/*     */     }
/*     */ 
/*     */     public boolean next() throws IOException
/*     */     {
/*  84 */       return adjust(this.spans.next());
/*     */     }
/*     */ 
/*     */     public boolean skipTo(int target) throws IOException
/*     */     {
/*  89 */       return adjust(this.spans.skipTo(target));
/*     */     }
/*     */ 
/*     */     private boolean adjust(boolean condition) {
/*  93 */       if (this.length != -1) {
/*  94 */         NearSpansUnordered.access$020(NearSpansUnordered.this, this.length);
/*     */       }
/*  96 */       if (condition) {
/*  97 */         this.length = (end() - start());
/*  98 */         NearSpansUnordered.access$012(NearSpansUnordered.this, this.length);
/*     */ 
/* 100 */         if ((NearSpansUnordered.this.max == null) || (doc() > NearSpansUnordered.this.max.doc()) || ((doc() == NearSpansUnordered.this.max.doc()) && (end() > NearSpansUnordered.this.max.end())))
/*     */         {
/* 102 */           NearSpansUnordered.access$102(NearSpansUnordered.this, this);
/*     */         }
/*     */       }
/* 105 */       NearSpansUnordered.access$202(NearSpansUnordered.this, condition);
/* 106 */       return condition;
/*     */     }
/*     */ 
/*     */     public int doc() {
/* 110 */       return this.spans.doc();
/*     */     }
/*     */     public int start() {
/* 113 */       return this.spans.start();
/*     */     }
/*     */     public int end() {
/* 116 */       return this.spans.end();
/*     */     }
/*     */ 
/*     */     public Collection<byte[]> getPayload() throws IOException {
/* 120 */       return new ArrayList(this.spans.getPayload());
/*     */     }
/*     */ 
/*     */     public boolean isPayloadAvailable()
/*     */     {
/* 126 */       return this.spans.isPayloadAvailable();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 130 */       return this.spans.toString() + "#" + this.index;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CellQueue extends PriorityQueue<NearSpansUnordered.SpansCell>
/*     */   {
/*     */     public CellQueue(int size)
/*     */     {
/*  56 */       initialize(size);
/*     */     }
/*     */ 
/*     */     protected final boolean lessThan(NearSpansUnordered.SpansCell spans1, NearSpansUnordered.SpansCell spans2)
/*     */     {
/*  61 */       if (spans1.doc() == spans2.doc()) {
/*  62 */         return NearSpansOrdered.docSpansOrdered(spans1, spans2);
/*     */       }
/*  64 */       return spans1.doc() < spans2.doc();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.NearSpansUnordered
 * JD-Core Version:    0.6.0
 */