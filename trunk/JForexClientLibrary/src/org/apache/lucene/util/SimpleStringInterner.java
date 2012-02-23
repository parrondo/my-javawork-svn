/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ public class SimpleStringInterner extends StringInterner
/*    */ {
/*    */   private final Entry[] cache;
/*    */   private final int maxChainLength;
/*    */ 
/*    */   public SimpleStringInterner(int tableSize, int maxChainLength)
/*    */   {
/* 48 */     this.cache = new Entry[Math.max(1, BitUtil.nextHighestPowerOfTwo(tableSize))];
/* 49 */     this.maxChainLength = Math.max(2, maxChainLength);
/*    */   }
/*    */ 
/*    */   public String intern(String s)
/*    */   {
/* 54 */     int h = s.hashCode();
/*    */ 
/* 57 */     int slot = h & this.cache.length - 1;
/*    */ 
/* 59 */     Entry first = this.cache[slot];
/* 60 */     Entry nextToLast = null;
/*    */ 
/* 62 */     int chainLength = 0;
/*    */ 
/* 64 */     for (Entry e = first; e != null; e = e.next) {
/* 65 */       if ((e.hash == h) && ((e.str == s) || (e.str.compareTo(s) == 0)))
/*    */       {
/* 67 */         return e.str;
/*    */       }
/*    */ 
/* 70 */       chainLength++;
/* 71 */       if (e.next != null) {
/* 72 */         nextToLast = e;
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 77 */     s = s.intern();
/* 78 */     this.cache[slot] = new Entry(s, h, first, null);
/* 79 */     if (chainLength >= this.maxChainLength)
/*    */     {
/* 81 */       Entry.access$002(nextToLast, null);
/*    */     }
/* 83 */     return s;
/*    */   }
/*    */ 
/*    */   private static class Entry
/*    */   {
/*    */     private final String str;
/*    */     private final int hash;
/*    */     private Entry next;
/*    */ 
/*    */     private Entry(String str, int hash, Entry next)
/*    */     {
/* 34 */       this.str = str;
/* 35 */       this.hash = hash;
/* 36 */       this.next = next;
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.SimpleStringInterner
 * JD-Core Version:    0.6.0
 */