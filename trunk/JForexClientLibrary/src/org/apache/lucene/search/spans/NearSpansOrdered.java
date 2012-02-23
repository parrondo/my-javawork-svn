/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ 
/*     */ public class NearSpansOrdered extends Spans
/*     */ {
/*     */   private final int allowedSlop;
/*  56 */   private boolean firstTime = true;
/*  57 */   private boolean more = false;
/*     */   private final Spans[] subSpans;
/*  63 */   private boolean inSameDoc = false;
/*     */ 
/*  65 */   private int matchDoc = -1;
/*  66 */   private int matchStart = -1;
/*  67 */   private int matchEnd = -1;
/*     */   private List<byte[]> matchPayload;
/*     */   private final Spans[] subSpansByDoc;
/*  71 */   private final Comparator<Spans> spanDocComparator = new Comparator() {
/*     */     public int compare(Spans o1, Spans o2) {
/*  73 */       return o1.doc() - o2.doc();
/*     */     }
/*  71 */   };
/*     */   private SpanNearQuery query;
/*  78 */   private boolean collectPayloads = true;
/*     */ 
/*     */   public NearSpansOrdered(SpanNearQuery spanNearQuery, IndexReader reader) throws IOException {
/*  81 */     this(spanNearQuery, reader, true);
/*     */   }
/*     */ 
/*     */   public NearSpansOrdered(SpanNearQuery spanNearQuery, IndexReader reader, boolean collectPayloads) throws IOException
/*     */   {
/*  86 */     if (spanNearQuery.getClauses().length < 2) {
/*  87 */       throw new IllegalArgumentException("Less than 2 clauses: " + spanNearQuery);
/*     */     }
/*     */ 
/*  90 */     this.collectPayloads = collectPayloads;
/*  91 */     this.allowedSlop = spanNearQuery.getSlop();
/*  92 */     SpanQuery[] clauses = spanNearQuery.getClauses();
/*  93 */     this.subSpans = new Spans[clauses.length];
/*  94 */     this.matchPayload = new LinkedList();
/*  95 */     this.subSpansByDoc = new Spans[clauses.length];
/*  96 */     for (int i = 0; i < clauses.length; i++) {
/*  97 */       this.subSpans[i] = clauses[i].getSpans(reader);
/*  98 */       this.subSpansByDoc[i] = this.subSpans[i];
/*     */     }
/* 100 */     this.query = spanNearQuery;
/*     */   }
/*     */ 
/*     */   public int doc()
/*     */   {
/* 105 */     return this.matchDoc;
/*     */   }
/*     */ 
/*     */   public int start() {
/* 109 */     return this.matchStart;
/*     */   }
/*     */ 
/*     */   public int end() {
/* 113 */     return this.matchEnd;
/*     */   }
/*     */   public Spans[] getSubSpans() {
/* 116 */     return this.subSpans;
/*     */   }
/*     */ 
/*     */   public Collection<byte[]> getPayload()
/*     */     throws IOException
/*     */   {
/* 123 */     return this.matchPayload;
/*     */   }
/*     */ 
/*     */   public boolean isPayloadAvailable()
/*     */   {
/* 129 */     return !this.matchPayload.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean next()
/*     */     throws IOException
/*     */   {
/* 135 */     if (this.firstTime) {
/* 136 */       this.firstTime = false;
/* 137 */       for (int i = 0; i < this.subSpans.length; i++) {
/* 138 */         if (!this.subSpans[i].next()) {
/* 139 */           this.more = false;
/* 140 */           return false;
/*     */         }
/*     */       }
/* 143 */       this.more = true;
/*     */     }
/* 145 */     if (this.collectPayloads) {
/* 146 */       this.matchPayload.clear();
/*     */     }
/* 148 */     return advanceAfterOrdered();
/*     */   }
/*     */ 
/*     */   public boolean skipTo(int target)
/*     */     throws IOException
/*     */   {
/* 154 */     if (this.firstTime) {
/* 155 */       this.firstTime = false;
/* 156 */       for (int i = 0; i < this.subSpans.length; i++) {
/* 157 */         if (!this.subSpans[i].skipTo(target)) {
/* 158 */           this.more = false;
/* 159 */           return false;
/*     */         }
/*     */       }
/* 162 */       this.more = true;
/* 163 */     } else if ((this.more) && (this.subSpans[0].doc() < target)) {
/* 164 */       if (this.subSpans[0].skipTo(target)) {
/* 165 */         this.inSameDoc = false;
/*     */       } else {
/* 167 */         this.more = false;
/* 168 */         return false;
/*     */       }
/*     */     }
/* 171 */     if (this.collectPayloads) {
/* 172 */       this.matchPayload.clear();
/*     */     }
/* 174 */     return advanceAfterOrdered();
/*     */   }
/*     */ 
/*     */   private boolean advanceAfterOrdered()
/*     */     throws IOException
/*     */   {
/* 182 */     while ((this.more) && ((this.inSameDoc) || (toSameDoc()))) {
/* 183 */       if ((stretchToOrder()) && (shrinkToAfterShortestMatch())) {
/* 184 */         return true;
/*     */       }
/*     */     }
/* 187 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean toSameDoc()
/*     */     throws IOException
/*     */   {
/* 193 */     ArrayUtil.mergeSort(this.subSpansByDoc, this.spanDocComparator);
/* 194 */     int firstIndex = 0;
/* 195 */     int maxDoc = this.subSpansByDoc[(this.subSpansByDoc.length - 1)].doc();
/* 196 */     while (this.subSpansByDoc[firstIndex].doc() != maxDoc) {
/* 197 */       if (!this.subSpansByDoc[firstIndex].skipTo(maxDoc)) {
/* 198 */         this.more = false;
/* 199 */         this.inSameDoc = false;
/* 200 */         return false;
/*     */       }
/* 202 */       maxDoc = this.subSpansByDoc[firstIndex].doc();
/* 203 */       firstIndex++; if (firstIndex == this.subSpansByDoc.length) {
/* 204 */         firstIndex = 0;
/*     */       }
/*     */     }
/* 207 */     for (int i = 0; i < this.subSpansByDoc.length; i++)
/*     */     {
/* 209 */       assert (this.subSpansByDoc[i].doc() == maxDoc) : (" NearSpansOrdered.toSameDoc() spans " + this.subSpansByDoc[0] + "\n at doc " + this.subSpansByDoc[i].doc() + ", but should be at " + maxDoc);
/*     */     }
/*     */ 
/* 213 */     this.inSameDoc = true;
/* 214 */     return true;
/*     */   }
/*     */ 
/*     */   static final boolean docSpansOrdered(Spans spans1, Spans spans2)
/*     */   {
/* 225 */     assert (spans1.doc() == spans2.doc()) : ("doc1 " + spans1.doc() + " != doc2 " + spans2.doc());
/* 226 */     int start1 = spans1.start();
/* 227 */     int start2 = spans2.start();
/*     */ 
/* 229 */     return spans1.end() < spans2.end();
/*     */   }
/*     */ 
/*     */   private static final boolean docSpansOrdered(int start1, int end1, int start2, int end2)
/*     */   {
/* 236 */     return end1 < end2;
/*     */   }
/*     */ 
/*     */   private boolean stretchToOrder()
/*     */     throws IOException
/*     */   {
/* 243 */     this.matchDoc = this.subSpans[0].doc();
/* 244 */     for (int i = 1; (this.inSameDoc) && (i < this.subSpans.length); i++) {
/* 245 */       while (!docSpansOrdered(this.subSpans[(i - 1)], this.subSpans[i])) {
/* 246 */         if (!this.subSpans[i].next()) {
/* 247 */           this.inSameDoc = false;
/* 248 */           this.more = false;
/*     */         }
/* 250 */         else if (this.matchDoc != this.subSpans[i].doc()) {
/* 251 */           this.inSameDoc = false;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 256 */     return this.inSameDoc;
/*     */   }
/*     */ 
/*     */   private boolean shrinkToAfterShortestMatch()
/*     */     throws IOException
/*     */   {
/* 264 */     this.matchStart = this.subSpans[(this.subSpans.length - 1)].start();
/* 265 */     this.matchEnd = this.subSpans[(this.subSpans.length - 1)].end();
/* 266 */     Set possibleMatchPayloads = new HashSet();
/* 267 */     if (this.subSpans[(this.subSpans.length - 1)].isPayloadAvailable()) {
/* 268 */       possibleMatchPayloads.addAll(this.subSpans[(this.subSpans.length - 1)].getPayload());
/*     */     }
/*     */ 
/* 271 */     Collection possiblePayload = null;
/*     */ 
/* 273 */     int matchSlop = 0;
/* 274 */     int lastStart = this.matchStart;
/* 275 */     int lastEnd = this.matchEnd;
/* 276 */     for (int i = this.subSpans.length - 2; i >= 0; i--) {
/* 277 */       Spans prevSpans = this.subSpans[i];
/* 278 */       if ((this.collectPayloads) && (prevSpans.isPayloadAvailable())) {
/* 279 */         Collection payload = prevSpans.getPayload();
/* 280 */         possiblePayload = new ArrayList(payload.size());
/* 281 */         possiblePayload.addAll(payload);
/*     */       }
/*     */ 
/* 284 */       int prevStart = prevSpans.start();
/* 285 */       int prevEnd = prevSpans.end();
/*     */       while (true) {
/* 287 */         if (!prevSpans.next()) {
/* 288 */           this.inSameDoc = false;
/* 289 */           this.more = false;
/* 290 */           break;
/* 291 */         }if (this.matchDoc != prevSpans.doc()) {
/* 292 */           this.inSameDoc = false;
/* 293 */           break;
/*     */         }
/* 295 */         int ppStart = prevSpans.start();
/* 296 */         int ppEnd = prevSpans.end();
/* 297 */         if (!docSpansOrdered(ppStart, ppEnd, lastStart, lastEnd)) {
/*     */           break;
/*     */         }
/* 300 */         prevStart = ppStart;
/* 301 */         prevEnd = ppEnd;
/* 302 */         if ((this.collectPayloads) && (prevSpans.isPayloadAvailable())) {
/* 303 */           Collection payload = prevSpans.getPayload();
/* 304 */           possiblePayload = new ArrayList(payload.size());
/* 305 */           possiblePayload.addAll(payload);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 311 */       if ((this.collectPayloads) && (possiblePayload != null)) {
/* 312 */         possibleMatchPayloads.addAll(possiblePayload);
/*     */       }
/*     */ 
/* 315 */       assert (prevStart <= this.matchStart);
/* 316 */       if (this.matchStart > prevEnd) {
/* 317 */         matchSlop += this.matchStart - prevEnd;
/*     */       }
/*     */ 
/* 323 */       this.matchStart = prevStart;
/* 324 */       lastStart = prevStart;
/* 325 */       lastEnd = prevEnd;
/*     */     }
/*     */ 
/* 328 */     boolean match = matchSlop <= this.allowedSlop;
/*     */ 
/* 330 */     if ((this.collectPayloads) && (match) && (possibleMatchPayloads.size() > 0)) {
/* 331 */       this.matchPayload.addAll(possibleMatchPayloads);
/*     */     }
/*     */ 
/* 334 */     return match;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 339 */     return getClass().getName() + "(" + this.query.toString() + ")@" + (this.more ? doc() + ":" + start() + "-" + end() : this.firstTime ? "START" : "END");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.NearSpansOrdered
 * JD-Core Version:    0.6.0
 */