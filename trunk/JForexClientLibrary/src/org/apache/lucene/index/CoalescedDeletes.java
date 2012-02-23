/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ 
/*     */ class CoalescedDeletes
/*     */ {
/*     */   final Map<Query, Integer> queries;
/*     */   final List<Iterable<Term>> iterables;
/*     */ 
/*     */   CoalescedDeletes()
/*     */   {
/*  31 */     this.queries = new HashMap();
/*  32 */     this.iterables = new ArrayList();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  37 */     return "CoalescedDeletes(termSets=" + this.iterables.size() + ",queries=" + this.queries.size() + ")";
/*     */   }
/*     */ 
/*     */   void update(FrozenBufferedDeletes in) {
/*  41 */     this.iterables.add(in.termsIterable());
/*     */ 
/*  43 */     for (int queryIdx = 0; queryIdx < in.queries.length; queryIdx++) {
/*  44 */       Query query = in.queries[queryIdx];
/*  45 */       this.queries.put(query, BufferedDeletes.MAX_INT);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Iterable<Term> termsIterable() {
/*  50 */     return new Iterable() {
/*     */       public Iterator<Term> iterator() {
/*  52 */         ArrayList subs = new ArrayList(CoalescedDeletes.this.iterables.size());
/*  53 */         for (Iterable iterable : CoalescedDeletes.this.iterables) {
/*  54 */           subs.add(iterable.iterator());
/*     */         }
/*  56 */         return CoalescedDeletes.mergedIterator(subs);
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   public Iterable<BufferedDeletesStream.QueryAndLimit> queriesIterable() {
/*  62 */     return new Iterable()
/*     */     {
/*     */       public Iterator<BufferedDeletesStream.QueryAndLimit> iterator() {
/*  65 */         return new Iterator() {
/*  66 */           private final Iterator<Map.Entry<Query, Integer>> iter = CoalescedDeletes.this.queries.entrySet().iterator();
/*     */ 
/*     */           public boolean hasNext() {
/*  69 */             return this.iter.hasNext();
/*     */           }
/*     */ 
/*     */           public BufferedDeletesStream.QueryAndLimit next() {
/*  73 */             Map.Entry ent = (Map.Entry)this.iter.next();
/*  74 */             return new BufferedDeletesStream.QueryAndLimit((Query)ent.getKey(), ((Integer)ent.getValue()).intValue());
/*     */           }
/*     */ 
/*     */           public void remove() {
/*  78 */             throw new UnsupportedOperationException();
/*     */           } } ;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static Iterator<Term> mergedIterator(List<Iterator<Term>> iterators) {
/*  87 */     return new Iterator(iterators)
/*     */     {
/*     */       Term current;
/*     */       CoalescedDeletes.TermMergeQueue queue;
/*     */       CoalescedDeletes.SubIterator[] top;
/*     */       int numTop;
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/* 107 */         if (this.queue.size() > 0) {
/* 108 */           return true;
/*     */         }
/*     */ 
/* 111 */         for (int i = 0; i < this.numTop; i++) {
/* 112 */           if (this.top[i].iterator.hasNext()) {
/* 113 */             return true;
/*     */           }
/*     */         }
/* 116 */         return false;
/*     */       }
/*     */ 
/*     */       public Term next()
/*     */       {
/* 121 */         pushTop();
/*     */ 
/* 124 */         if (this.queue.size() > 0)
/* 125 */           pullTop();
/*     */         else {
/* 127 */           this.current = null;
/*     */         }
/* 129 */         return this.current;
/*     */       }
/*     */ 
/*     */       public void remove() {
/* 133 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       private void pullTop()
/*     */       {
/* 138 */         assert (this.numTop == 0);
/*     */         while (true) {
/* 140 */           this.top[(this.numTop++)] = ((CoalescedDeletes.SubIterator)this.queue.pop());
/* 141 */           if (this.queue.size() == 0) break; if (((CoalescedDeletes.SubIterator)this.queue.top()).current.equals(this.top[0].current))
/*     */           {
/*     */             continue;
/*     */           }
/*     */         }
/* 146 */         this.current = this.top[0].current;
/*     */       }
/*     */ 
/*     */       private void pushTop()
/*     */       {
/* 151 */         for (int i = 0; i < this.numTop; i++) {
/* 152 */           if (this.top[i].iterator.hasNext()) {
/* 153 */             this.top[i].current = ((Term)this.top[i].iterator.next());
/* 154 */             this.queue.add(this.top[i]);
/*     */           }
/*     */           else {
/* 157 */             this.top[i].current = null;
/*     */           }
/*     */         }
/* 160 */         this.numTop = 0;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static class TermMergeQueue extends PriorityQueue<CoalescedDeletes.SubIterator>
/*     */   {
/*     */     TermMergeQueue(int size)
/*     */     {
/* 173 */       initialize(size);
/*     */     }
/*     */ 
/*     */     protected boolean lessThan(CoalescedDeletes.SubIterator a, CoalescedDeletes.SubIterator b)
/*     */     {
/* 178 */       int cmp = a.current.compareTo(b.current);
/* 179 */       if (cmp != 0) {
/* 180 */         return cmp < 0;
/*     */       }
/* 182 */       return a.index < b.index;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SubIterator
/*     */   {
/*     */     Iterator<Term> iterator;
/*     */     Term current;
/*     */     int index;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.CoalescedDeletes
 * JD-Core Version:    0.6.0
 */