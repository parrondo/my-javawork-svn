/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ 
/*     */ public abstract class TopFieldCollector extends TopDocsCollector<FieldValueHitQueue.Entry>
/*     */ {
/* 842 */   private static final ScoreDoc[] EMPTY_SCOREDOCS = new ScoreDoc[0];
/*     */   private final boolean fillFields;
/* 850 */   float maxScore = (0.0F / 0.0F);
/*     */   final int numHits;
/* 853 */   FieldValueHitQueue.Entry bottom = null;
/*     */   boolean queueFull;
/*     */   int docBase;
/*     */ 
/*     */   private TopFieldCollector(PriorityQueue<FieldValueHitQueue.Entry> pq, int numHits, boolean fillFields)
/*     */   {
/* 863 */     super(pq);
/* 864 */     this.numHits = numHits;
/* 865 */     this.fillFields = fillFields;
/*     */   }
/*     */ 
/*     */   public static TopFieldCollector create(Sort sort, int numHits, boolean fillFields, boolean trackDocScores, boolean trackMaxScore, boolean docsScoredInOrder)
/*     */     throws IOException
/*     */   {
/* 908 */     if (sort.fields.length == 0) {
/* 909 */       throw new IllegalArgumentException("Sort must contain at least one field");
/*     */     }
/*     */ 
/* 912 */     if (numHits <= 0) {
/* 913 */       throw new IllegalArgumentException("numHits must be > 0; please use TotalHitCountCollector if you just need the total hit count");
/*     */     }
/*     */ 
/* 916 */     FieldValueHitQueue queue = FieldValueHitQueue.create(sort.fields, numHits);
/* 917 */     if (queue.getComparators().length == 1) {
/* 918 */       if (docsScoredInOrder) {
/* 919 */         if (trackMaxScore)
/* 920 */           return new OneComparatorScoringMaxScoreCollector(queue, numHits, fillFields);
/* 921 */         if (trackDocScores) {
/* 922 */           return new OneComparatorScoringNoMaxScoreCollector(queue, numHits, fillFields);
/*     */         }
/* 924 */         return new OneComparatorNonScoringCollector(queue, numHits, fillFields);
/*     */       }
/*     */ 
/* 927 */       if (trackMaxScore)
/* 928 */         return new OutOfOrderOneComparatorScoringMaxScoreCollector(queue, numHits, fillFields);
/* 929 */       if (trackDocScores) {
/* 930 */         return new OutOfOrderOneComparatorScoringNoMaxScoreCollector(queue, numHits, fillFields);
/*     */       }
/* 932 */       return new OutOfOrderOneComparatorNonScoringCollector(queue, numHits, fillFields);
/*     */     }
/*     */ 
/* 938 */     if (docsScoredInOrder) {
/* 939 */       if (trackMaxScore)
/* 940 */         return new MultiComparatorScoringMaxScoreCollector(queue, numHits, fillFields);
/* 941 */       if (trackDocScores) {
/* 942 */         return new MultiComparatorScoringNoMaxScoreCollector(queue, numHits, fillFields);
/*     */       }
/* 944 */       return new MultiComparatorNonScoringCollector(queue, numHits, fillFields);
/*     */     }
/*     */ 
/* 947 */     if (trackMaxScore)
/* 948 */       return new OutOfOrderMultiComparatorScoringMaxScoreCollector(queue, numHits, fillFields);
/* 949 */     if (trackDocScores) {
/* 950 */       return new OutOfOrderMultiComparatorScoringNoMaxScoreCollector(queue, numHits, fillFields);
/*     */     }
/* 952 */     return new OutOfOrderMultiComparatorNonScoringCollector(queue, numHits, fillFields);
/*     */   }
/*     */ 
/*     */   final void add(int slot, int doc, float score)
/*     */   {
/* 958 */     this.bottom = ((FieldValueHitQueue.Entry)this.pq.add(new FieldValueHitQueue.Entry(slot, this.docBase + doc, score)));
/* 959 */     this.queueFull = (this.totalHits == this.numHits);
/*     */   }
/*     */ 
/*     */   protected void populateResults(ScoreDoc[] results, int howMany)
/*     */   {
/* 969 */     if (this.fillFields)
/*     */     {
/* 971 */       FieldValueHitQueue queue = (FieldValueHitQueue)this.pq;
/* 972 */       for (int i = howMany - 1; i >= 0; i--)
/* 973 */         results[i] = queue.fillFields((FieldValueHitQueue.Entry)queue.pop());
/*     */     }
/*     */     else {
/* 976 */       for (int i = howMany - 1; i >= 0; i--) {
/* 977 */         FieldValueHitQueue.Entry entry = (FieldValueHitQueue.Entry)this.pq.pop();
/* 978 */         results[i] = new FieldDoc(entry.doc, entry.score);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected TopDocs newTopDocs(ScoreDoc[] results, int start)
/*     */   {
/* 985 */     if (results == null) {
/* 986 */       results = EMPTY_SCOREDOCS;
/*     */ 
/* 988 */       this.maxScore = (0.0F / 0.0F);
/*     */     }
/*     */ 
/* 992 */     return new TopFieldDocs(this.totalHits, results, ((FieldValueHitQueue)this.pq).getFields(), this.maxScore);
/*     */   }
/*     */ 
/*     */   public boolean acceptsDocsOutOfOrder()
/*     */   {
/* 997 */     return false;
/*     */   }
/*     */ 
/*     */   private static final class OutOfOrderMultiComparatorScoringNoMaxScoreCollector extends TopFieldCollector.MultiComparatorScoringNoMaxScoreCollector
/*     */   {
/*     */     public OutOfOrderMultiComparatorScoringNoMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 772 */       super(numHits, fillFields);
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 777 */       this.totalHits += 1;
/* 778 */       if (this.queueFull)
/*     */       {
/* 780 */         for (int i = 0; ; i++) {
/* 781 */           int c = this.reverseMul[i] * this.comparators[i].compareBottom(doc);
/* 782 */           if (c < 0)
/*     */           {
/* 784 */             return;
/* 785 */           }if (c > 0) {
/*     */             break;
/*     */           }
/* 788 */           if (i != this.comparators.length - 1)
/*     */             continue;
/* 790 */           if (doc + this.docBase <= this.bottom.doc)
/*     */             break;
/* 792 */           return;
/*     */         }
/*     */ 
/* 799 */         for (int i = 0; i < this.comparators.length; i++) {
/* 800 */           this.comparators[i].copy(this.bottom.slot, doc);
/*     */         }
/*     */ 
/* 804 */         float score = this.scorer.score();
/* 805 */         updateBottom(doc, score);
/*     */ 
/* 807 */         for (int i = 0; i < this.comparators.length; i++)
/* 808 */           this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */       else
/*     */       {
/* 812 */         int slot = this.totalHits - 1;
/*     */ 
/* 814 */         for (int i = 0; i < this.comparators.length; i++) {
/* 815 */           this.comparators[i].copy(slot, doc);
/*     */         }
/*     */ 
/* 819 */         float score = this.scorer.score();
/* 820 */         add(slot, doc, score);
/* 821 */         if (this.queueFull)
/* 822 */           for (int i = 0; i < this.comparators.length; i++)
/* 823 */             this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer)
/*     */       throws IOException
/*     */     {
/* 831 */       this.scorer = scorer;
/* 832 */       super.setScorer(scorer);
/*     */     }
/*     */ 
/*     */     public boolean acceptsDocsOutOfOrder()
/*     */     {
/* 837 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MultiComparatorScoringNoMaxScoreCollector extends TopFieldCollector.MultiComparatorNonScoringCollector
/*     */   {
/*     */     Scorer scorer;
/*     */ 
/*     */     public MultiComparatorScoringNoMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 693 */       super(numHits, fillFields);
/*     */     }
/*     */ 
/*     */     final void updateBottom(int doc, float score) {
/* 697 */       this.bottom.doc = (this.docBase + doc);
/* 698 */       this.bottom.score = score;
/* 699 */       this.bottom = ((FieldValueHitQueue.Entry)this.pq.updateTop());
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 704 */       this.totalHits += 1;
/* 705 */       if (this.queueFull)
/*     */       {
/* 707 */         for (int i = 0; ; i++) {
/* 708 */           int c = this.reverseMul[i] * this.comparators[i].compareBottom(doc);
/* 709 */           if (c < 0)
/*     */           {
/* 711 */             return;
/* 712 */           }if (c > 0) {
/*     */             break;
/*     */           }
/* 715 */           if (i == this.comparators.length - 1)
/*     */           {
/* 719 */             return;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 724 */         for (int i = 0; i < this.comparators.length; i++) {
/* 725 */           this.comparators[i].copy(this.bottom.slot, doc);
/*     */         }
/*     */ 
/* 729 */         float score = this.scorer.score();
/* 730 */         updateBottom(doc, score);
/*     */ 
/* 732 */         for (int i = 0; i < this.comparators.length; i++)
/* 733 */           this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */       else
/*     */       {
/* 737 */         int slot = this.totalHits - 1;
/*     */ 
/* 739 */         for (int i = 0; i < this.comparators.length; i++) {
/* 740 */           this.comparators[i].copy(slot, doc);
/*     */         }
/*     */ 
/* 744 */         float score = this.scorer.score();
/* 745 */         add(slot, doc, score);
/* 746 */         if (this.queueFull)
/* 747 */           for (int i = 0; i < this.comparators.length; i++)
/* 748 */             this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer)
/*     */       throws IOException
/*     */     {
/* 756 */       this.scorer = scorer;
/* 757 */       super.setScorer(scorer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class OutOfOrderMultiComparatorScoringMaxScoreCollector extends TopFieldCollector.MultiComparatorScoringMaxScoreCollector
/*     */   {
/*     */     public OutOfOrderMultiComparatorScoringMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 620 */       super(numHits, fillFields);
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 625 */       float score = this.scorer.score();
/* 626 */       if (score > this.maxScore) {
/* 627 */         this.maxScore = score;
/*     */       }
/* 629 */       this.totalHits += 1;
/* 630 */       if (this.queueFull)
/*     */       {
/* 632 */         for (int i = 0; ; i++) {
/* 633 */           int c = this.reverseMul[i] * this.comparators[i].compareBottom(doc);
/* 634 */           if (c < 0)
/*     */           {
/* 636 */             return;
/* 637 */           }if (c > 0) {
/*     */             break;
/*     */           }
/* 640 */           if (i != this.comparators.length - 1)
/*     */             continue;
/* 642 */           if (doc + this.docBase <= this.bottom.doc)
/*     */             break;
/* 644 */           return;
/*     */         }
/*     */ 
/* 651 */         for (int i = 0; i < this.comparators.length; i++) {
/* 652 */           this.comparators[i].copy(this.bottom.slot, doc);
/*     */         }
/*     */ 
/* 655 */         updateBottom(doc, score);
/*     */ 
/* 657 */         for (int i = 0; i < this.comparators.length; i++)
/* 658 */           this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */       else
/*     */       {
/* 662 */         int slot = this.totalHits - 1;
/*     */ 
/* 664 */         for (int i = 0; i < this.comparators.length; i++) {
/* 665 */           this.comparators[i].copy(slot, doc);
/*     */         }
/* 667 */         add(slot, doc, score);
/* 668 */         if (this.queueFull)
/* 669 */           for (int i = 0; i < this.comparators.length; i++)
/* 670 */             this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean acceptsDocsOutOfOrder()
/*     */     {
/* 678 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MultiComparatorScoringMaxScoreCollector extends TopFieldCollector.MultiComparatorNonScoringCollector
/*     */   {
/*     */     Scorer scorer;
/*     */ 
/*     */     public MultiComparatorScoringMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 541 */       super(numHits, fillFields);
/*     */ 
/* 543 */       this.maxScore = (1.0F / -1.0F);
/*     */     }
/*     */ 
/*     */     final void updateBottom(int doc, float score) {
/* 547 */       this.bottom.doc = (this.docBase + doc);
/* 548 */       this.bottom.score = score;
/* 549 */       this.bottom = ((FieldValueHitQueue.Entry)this.pq.updateTop());
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 554 */       float score = this.scorer.score();
/* 555 */       if (score > this.maxScore) {
/* 556 */         this.maxScore = score;
/*     */       }
/* 558 */       this.totalHits += 1;
/* 559 */       if (this.queueFull)
/*     */       {
/* 561 */         for (int i = 0; ; i++) {
/* 562 */           int c = this.reverseMul[i] * this.comparators[i].compareBottom(doc);
/* 563 */           if (c < 0)
/*     */           {
/* 565 */             return;
/* 566 */           }if (c > 0) {
/*     */             break;
/*     */           }
/* 569 */           if (i == this.comparators.length - 1)
/*     */           {
/* 573 */             return;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 578 */         for (int i = 0; i < this.comparators.length; i++) {
/* 579 */           this.comparators[i].copy(this.bottom.slot, doc);
/*     */         }
/*     */ 
/* 582 */         updateBottom(doc, score);
/*     */ 
/* 584 */         for (int i = 0; i < this.comparators.length; i++)
/* 585 */           this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */       else
/*     */       {
/* 589 */         int slot = this.totalHits - 1;
/*     */ 
/* 591 */         for (int i = 0; i < this.comparators.length; i++) {
/* 592 */           this.comparators[i].copy(slot, doc);
/*     */         }
/* 594 */         add(slot, doc, score);
/* 595 */         if (this.queueFull)
/* 596 */           for (int i = 0; i < this.comparators.length; i++)
/* 597 */             this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer)
/*     */       throws IOException
/*     */     {
/* 605 */       this.scorer = scorer;
/* 606 */       super.setScorer(scorer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OutOfOrderMultiComparatorNonScoringCollector extends TopFieldCollector.MultiComparatorNonScoringCollector
/*     */   {
/*     */     public OutOfOrderMultiComparatorNonScoringCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 472 */       super(numHits, fillFields);
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 477 */       this.totalHits += 1;
/* 478 */       if (this.queueFull)
/*     */       {
/* 480 */         for (int i = 0; ; i++) {
/* 481 */           int c = this.reverseMul[i] * this.comparators[i].compareBottom(doc);
/* 482 */           if (c < 0)
/*     */           {
/* 484 */             return;
/* 485 */           }if (c > 0) {
/*     */             break;
/*     */           }
/* 488 */           if (i != this.comparators.length - 1)
/*     */             continue;
/* 490 */           if (doc + this.docBase <= this.bottom.doc)
/*     */             break;
/* 492 */           return;
/*     */         }
/*     */ 
/* 499 */         for (int i = 0; i < this.comparators.length; i++) {
/* 500 */           this.comparators[i].copy(this.bottom.slot, doc);
/*     */         }
/*     */ 
/* 503 */         updateBottom(doc);
/*     */ 
/* 505 */         for (int i = 0; i < this.comparators.length; i++)
/* 506 */           this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */       else
/*     */       {
/* 510 */         int slot = this.totalHits - 1;
/*     */ 
/* 512 */         for (int i = 0; i < this.comparators.length; i++) {
/* 513 */           this.comparators[i].copy(slot, doc);
/*     */         }
/* 515 */         add(slot, doc, (0.0F / 0.0F));
/* 516 */         if (this.queueFull)
/* 517 */           for (int i = 0; i < this.comparators.length; i++)
/* 518 */             this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean acceptsDocsOutOfOrder()
/*     */     {
/* 526 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MultiComparatorNonScoringCollector extends TopFieldCollector
/*     */   {
/*     */     final FieldComparator[] comparators;
/*     */     final int[] reverseMul;
/*     */ 
/*     */     public MultiComparatorNonScoringCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 387 */       super(numHits, fillFields, null);
/* 388 */       this.comparators = queue.getComparators();
/* 389 */       this.reverseMul = queue.getReverseMul();
/*     */     }
/*     */ 
/*     */     final void updateBottom(int doc)
/*     */     {
/* 394 */       this.bottom.doc = (this.docBase + doc);
/* 395 */       this.bottom = ((FieldValueHitQueue.Entry)this.pq.updateTop());
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 400 */       this.totalHits += 1;
/* 401 */       if (this.queueFull)
/*     */       {
/* 403 */         for (int i = 0; ; i++) {
/* 404 */           int c = this.reverseMul[i] * this.comparators[i].compareBottom(doc);
/* 405 */           if (c < 0)
/*     */           {
/* 407 */             return;
/* 408 */           }if (c > 0) {
/*     */             break;
/*     */           }
/* 411 */           if (i == this.comparators.length - 1)
/*     */           {
/* 415 */             return;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 420 */         for (int i = 0; i < this.comparators.length; i++) {
/* 421 */           this.comparators[i].copy(this.bottom.slot, doc);
/*     */         }
/*     */ 
/* 424 */         updateBottom(doc);
/*     */ 
/* 426 */         for (int i = 0; i < this.comparators.length; i++)
/* 427 */           this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */       else
/*     */       {
/* 431 */         int slot = this.totalHits - 1;
/*     */ 
/* 433 */         for (int i = 0; i < this.comparators.length; i++) {
/* 434 */           this.comparators[i].copy(slot, doc);
/*     */         }
/* 436 */         add(slot, doc, (0.0F / 0.0F));
/* 437 */         if (this.queueFull)
/* 438 */           for (int i = 0; i < this.comparators.length; i++)
/* 439 */             this.comparators[i].setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setNextReader(IndexReader reader, int docBase)
/*     */       throws IOException
/*     */     {
/* 447 */       this.docBase = docBase;
/* 448 */       for (int i = 0; i < this.comparators.length; i++)
/* 449 */         this.comparators[i].setNextReader(reader, docBase);
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer)
/*     */       throws IOException
/*     */     {
/* 456 */       for (int i = 0; i < this.comparators.length; i++)
/* 457 */         this.comparators[i].setScorer(scorer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OutOfOrderOneComparatorScoringMaxScoreCollector extends TopFieldCollector.OneComparatorScoringMaxScoreCollector
/*     */   {
/*     */     public OutOfOrderOneComparatorScoringMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 336 */       super(numHits, fillFields);
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 341 */       float score = this.scorer.score();
/* 342 */       if (score > this.maxScore) {
/* 343 */         this.maxScore = score;
/*     */       }
/* 345 */       this.totalHits += 1;
/* 346 */       if (this.queueFull)
/*     */       {
/* 348 */         int cmp = this.reverseMul * this.comparator.compareBottom(doc);
/* 349 */         if ((cmp < 0) || ((cmp == 0) && (doc + this.docBase > this.bottom.doc))) {
/* 350 */           return;
/*     */         }
/*     */ 
/* 354 */         this.comparator.copy(this.bottom.slot, doc);
/* 355 */         updateBottom(doc, score);
/* 356 */         this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */       else {
/* 359 */         int slot = this.totalHits - 1;
/*     */ 
/* 361 */         this.comparator.copy(slot, doc);
/* 362 */         add(slot, doc, score);
/* 363 */         if (this.queueFull)
/* 364 */           this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean acceptsDocsOutOfOrder()
/*     */     {
/* 371 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OneComparatorScoringMaxScoreCollector extends TopFieldCollector.OneComparatorNonScoringCollector
/*     */   {
/*     */     Scorer scorer;
/*     */ 
/*     */     public OneComparatorScoringMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 276 */       super(numHits, fillFields);
/*     */ 
/* 278 */       this.maxScore = (1.0F / -1.0F);
/*     */     }
/*     */ 
/*     */     final void updateBottom(int doc, float score) {
/* 282 */       this.bottom.doc = (this.docBase + doc);
/* 283 */       this.bottom.score = score;
/* 284 */       this.bottom = ((FieldValueHitQueue.Entry)this.pq.updateTop());
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 289 */       float score = this.scorer.score();
/* 290 */       if (score > this.maxScore) {
/* 291 */         this.maxScore = score;
/*     */       }
/* 293 */       this.totalHits += 1;
/* 294 */       if (this.queueFull) {
/* 295 */         if (this.reverseMul * this.comparator.compareBottom(doc) <= 0)
/*     */         {
/* 299 */           return;
/*     */         }
/*     */ 
/* 303 */         this.comparator.copy(this.bottom.slot, doc);
/* 304 */         updateBottom(doc, score);
/* 305 */         this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */       else {
/* 308 */         int slot = this.totalHits - 1;
/*     */ 
/* 310 */         this.comparator.copy(slot, doc);
/* 311 */         add(slot, doc, score);
/* 312 */         if (this.queueFull)
/* 313 */           this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer)
/*     */       throws IOException
/*     */     {
/* 321 */       this.scorer = scorer;
/* 322 */       super.setScorer(scorer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OutOfOrderOneComparatorScoringNoMaxScoreCollector extends TopFieldCollector.OneComparatorScoringNoMaxScoreCollector
/*     */   {
/*     */     public OutOfOrderOneComparatorScoringNoMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 223 */       super(numHits, fillFields);
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 228 */       this.totalHits += 1;
/* 229 */       if (this.queueFull)
/*     */       {
/* 231 */         int cmp = this.reverseMul * this.comparator.compareBottom(doc);
/* 232 */         if ((cmp < 0) || ((cmp == 0) && (doc + this.docBase > this.bottom.doc))) {
/* 233 */           return;
/*     */         }
/*     */ 
/* 237 */         float score = this.scorer.score();
/*     */ 
/* 240 */         this.comparator.copy(this.bottom.slot, doc);
/* 241 */         updateBottom(doc, score);
/* 242 */         this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */       else {
/* 245 */         float score = this.scorer.score();
/*     */ 
/* 248 */         int slot = this.totalHits - 1;
/*     */ 
/* 250 */         this.comparator.copy(slot, doc);
/* 251 */         add(slot, doc, score);
/* 252 */         if (this.queueFull)
/* 253 */           this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean acceptsDocsOutOfOrder()
/*     */     {
/* 260 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OneComparatorScoringNoMaxScoreCollector extends TopFieldCollector.OneComparatorNonScoringCollector
/*     */   {
/*     */     Scorer scorer;
/*     */ 
/*     */     public OneComparatorScoringNoMaxScoreCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 162 */       super(numHits, fillFields);
/*     */     }
/*     */ 
/*     */     final void updateBottom(int doc, float score) {
/* 166 */       this.bottom.doc = (this.docBase + doc);
/* 167 */       this.bottom.score = score;
/* 168 */       this.bottom = ((FieldValueHitQueue.Entry)this.pq.updateTop());
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 173 */       this.totalHits += 1;
/* 174 */       if (this.queueFull) {
/* 175 */         if (this.reverseMul * this.comparator.compareBottom(doc) <= 0)
/*     */         {
/* 179 */           return;
/*     */         }
/*     */ 
/* 183 */         float score = this.scorer.score();
/*     */ 
/* 186 */         this.comparator.copy(this.bottom.slot, doc);
/* 187 */         updateBottom(doc, score);
/* 188 */         this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */       else {
/* 191 */         float score = this.scorer.score();
/*     */ 
/* 194 */         int slot = this.totalHits - 1;
/*     */ 
/* 196 */         this.comparator.copy(slot, doc);
/* 197 */         add(slot, doc, score);
/* 198 */         if (this.queueFull)
/* 199 */           this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer)
/*     */       throws IOException
/*     */     {
/* 206 */       this.scorer = scorer;
/* 207 */       this.comparator.setScorer(scorer);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OutOfOrderOneComparatorNonScoringCollector extends TopFieldCollector.OneComparatorNonScoringCollector
/*     */   {
/*     */     public OutOfOrderOneComparatorNonScoringCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/* 115 */       super(numHits, fillFields);
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/* 120 */       this.totalHits += 1;
/* 121 */       if (this.queueFull)
/*     */       {
/* 123 */         int cmp = this.reverseMul * this.comparator.compareBottom(doc);
/* 124 */         if ((cmp < 0) || ((cmp == 0) && (doc + this.docBase > this.bottom.doc))) {
/* 125 */           return;
/*     */         }
/*     */ 
/* 129 */         this.comparator.copy(this.bottom.slot, doc);
/* 130 */         updateBottom(doc);
/* 131 */         this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */       else {
/* 134 */         int slot = this.totalHits - 1;
/*     */ 
/* 136 */         this.comparator.copy(slot, doc);
/* 137 */         add(slot, doc, (0.0F / 0.0F));
/* 138 */         if (this.queueFull)
/* 139 */           this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean acceptsDocsOutOfOrder()
/*     */     {
/* 146 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OneComparatorNonScoringCollector extends TopFieldCollector
/*     */   {
/*     */     final FieldComparator comparator;
/*     */     final int reverseMul;
/*     */ 
/*     */     public OneComparatorNonScoringCollector(FieldValueHitQueue<FieldValueHitQueue.Entry> queue, int numHits, boolean fillFields)
/*     */       throws IOException
/*     */     {
/*  54 */       super(numHits, fillFields, null);
/*  55 */       this.comparator = queue.getComparators()[0];
/*  56 */       this.reverseMul = queue.getReverseMul()[0];
/*     */     }
/*     */ 
/*     */     final void updateBottom(int doc)
/*     */     {
/*  61 */       this.bottom.doc = (this.docBase + doc);
/*  62 */       this.bottom = ((FieldValueHitQueue.Entry)this.pq.updateTop());
/*     */     }
/*     */ 
/*     */     public void collect(int doc) throws IOException
/*     */     {
/*  67 */       this.totalHits += 1;
/*  68 */       if (this.queueFull) {
/*  69 */         if (this.reverseMul * this.comparator.compareBottom(doc) <= 0)
/*     */         {
/*  73 */           return;
/*     */         }
/*     */ 
/*  77 */         this.comparator.copy(this.bottom.slot, doc);
/*  78 */         updateBottom(doc);
/*  79 */         this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */       else {
/*  82 */         int slot = this.totalHits - 1;
/*     */ 
/*  84 */         this.comparator.copy(slot, doc);
/*  85 */         add(slot, doc, (0.0F / 0.0F));
/*  86 */         if (this.queueFull)
/*  87 */           this.comparator.setBottom(this.bottom.slot);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setNextReader(IndexReader reader, int docBase)
/*     */       throws IOException
/*     */     {
/*  94 */       this.docBase = docBase;
/*  95 */       this.comparator.setNextReader(reader, docBase);
/*     */     }
/*     */ 
/*     */     public void setScorer(Scorer scorer) throws IOException
/*     */     {
/* 100 */       this.comparator.setScorer(scorer);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TopFieldCollector
 * JD-Core Version:    0.6.0
 */