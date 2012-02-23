/*    */ package org.eclipse.jdt.internal.compiler.util;
/*    */ 
/*    */ public final class HashtableOfLong
/*    */ {
/*    */   public long[] keyTable;
/*    */   public Object[] valueTable;
/*    */   public int elementSize;
/*    */   int threshold;
/*    */ 
/*    */   public HashtableOfLong()
/*    */   {
/* 25 */     this(13);
/*    */   }
/*    */   public HashtableOfLong(int size) {
/* 28 */     this.elementSize = 0;
/* 29 */     this.threshold = size;
/* 30 */     int extraRoom = (int)(size * 1.75F);
/* 31 */     if (this.threshold == extraRoom)
/* 32 */       extraRoom++;
/* 33 */     this.keyTable = new long[extraRoom];
/* 34 */     this.valueTable = new Object[extraRoom];
/*    */   }
/*    */   public boolean containsKey(long key) {
/* 37 */     int length = this.keyTable.length;
/* 38 */     int index = (int)(key >>> 32) % length;
/*    */     long currentKey;
/* 40 */     while ((currentKey = this.keyTable[index]) != 0L)
/*    */     {
/*    */       long currentKey;
/* 41 */       if (currentKey == key)
/* 42 */         return true;
/* 43 */       index++; if (index == length) {
/* 44 */         index = 0;
/*    */       }
/*    */     }
/* 47 */     return false;
/*    */   }
/*    */   public Object get(long key) {
/* 50 */     int length = this.keyTable.length;
/* 51 */     int index = (int)(key >>> 32) % length;
/*    */     long currentKey;
/* 53 */     while ((currentKey = this.keyTable[index]) != 0L)
/*    */     {
/* 54 */       long currentKey;
/* 54 */       if (currentKey == key) return this.valueTable[index];
/* 55 */       index++; if (index == length) {
/* 56 */         index = 0;
/*    */       }
/*    */     }
/* 59 */     return null;
/*    */   }
/*    */   public Object put(long key, Object value) {
/* 62 */     int length = this.keyTable.length;
/* 63 */     int index = (int)(key >>> 32) % length;
/*    */     long currentKey;
/* 65 */     while ((currentKey = this.keyTable[index]) != 0L)
/*    */     {
/* 66 */       long currentKey;
/* 66 */       if (currentKey == key) return this.valueTable[index] =  = value;
/* 67 */       index++; if (index == length) {
/* 68 */         index = 0;
/*    */       }
/*    */     }
/* 71 */     this.keyTable[index] = key;
/* 72 */     this.valueTable[index] = value;
/*    */ 
/* 75 */     if (++this.elementSize > this.threshold)
/* 76 */       rehash();
/* 77 */     return value;
/*    */   }
/*    */   private void rehash() {
/* 80 */     HashtableOfLong newHashtable = new HashtableOfLong(this.elementSize * 2);
/*    */ 
/* 82 */     int i = this.keyTable.length;
/*    */     do
/*    */     {
/*    */       long currentKey;
/* 83 */       if ((currentKey = this.keyTable[i]) != 0L)
/* 84 */         newHashtable.put(currentKey, this.valueTable[i]);
/* 82 */       i--; } while (i >= 0);
/*    */ 
/* 86 */     this.keyTable = newHashtable.keyTable;
/* 87 */     this.valueTable = newHashtable.valueTable;
/* 88 */     this.threshold = newHashtable.threshold;
/*    */   }
/*    */   public int size() {
/* 91 */     return this.elementSize;
/*    */   }
/*    */   public String toString() {
/* 94 */     String s = "";
/*    */ 
/* 96 */     int i = 0; for (int length = this.valueTable.length; i < length; i++)
/*    */     {
/*    */       Object object;
/* 97 */       if ((object = this.valueTable[i]) != null)
/* 98 */         s = s + this.keyTable[i] + " -> " + object.toString() + "\n"; 
/*    */     }
/* 99 */     return s;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.HashtableOfLong
 * JD-Core Version:    0.6.0
 */