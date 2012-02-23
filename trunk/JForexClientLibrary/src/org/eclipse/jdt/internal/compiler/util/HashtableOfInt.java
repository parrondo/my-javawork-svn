/*    */ package org.eclipse.jdt.internal.compiler.util;
/*    */ 
/*    */ public final class HashtableOfInt
/*    */ {
/*    */   public int[] keyTable;
/*    */   public Object[] valueTable;
/*    */   public int elementSize;
/*    */   int threshold;
/*    */ 
/*    */   public HashtableOfInt()
/*    */   {
/* 25 */     this(13);
/*    */   }
/*    */   public HashtableOfInt(int size) {
/* 28 */     this.elementSize = 0;
/* 29 */     this.threshold = size;
/* 30 */     int extraRoom = (int)(size * 1.75F);
/* 31 */     if (this.threshold == extraRoom)
/* 32 */       extraRoom++;
/* 33 */     this.keyTable = new int[extraRoom];
/* 34 */     this.valueTable = new Object[extraRoom];
/*    */   }
/*    */   public boolean containsKey(int key) {
/* 37 */     int length = this.keyTable.length; int index = key % length;
/*    */     int currentKey;
/* 39 */     while ((currentKey = this.keyTable[index]) != 0)
/*    */     {
/*    */       int currentKey;
/* 40 */       if (currentKey == key)
/* 41 */         return true;
/* 42 */       index++; if (index == length) {
/* 43 */         index = 0;
/*    */       }
/*    */     }
/* 46 */     return false;
/*    */   }
/*    */   public Object get(int key) {
/* 49 */     int length = this.keyTable.length; int index = key % length;
/*    */     int currentKey;
/* 51 */     while ((currentKey = this.keyTable[index]) != 0)
/*    */     {
/* 52 */       int currentKey;
/* 52 */       if (currentKey == key) return this.valueTable[index];
/* 53 */       index++; if (index == length) {
/* 54 */         index = 0;
/*    */       }
/*    */     }
/* 57 */     return null;
/*    */   }
/*    */   public Object put(int key, Object value) {
/* 60 */     int length = this.keyTable.length; int index = key % length;
/*    */     int currentKey;
/* 62 */     while ((currentKey = this.keyTable[index]) != 0)
/*    */     {
/* 63 */       int currentKey;
/* 63 */       if (currentKey == key) return this.valueTable[index] =  = value;
/* 64 */       index++; if (index == length) {
/* 65 */         index = 0;
/*    */       }
/*    */     }
/* 68 */     this.keyTable[index] = key;
/* 69 */     this.valueTable[index] = value;
/*    */ 
/* 72 */     if (++this.elementSize > this.threshold)
/* 73 */       rehash();
/* 74 */     return value;
/*    */   }
/*    */   private void rehash() {
/* 77 */     HashtableOfInt newHashtable = new HashtableOfInt(this.elementSize * 2);
/*    */ 
/* 79 */     int i = this.keyTable.length;
/*    */     do
/*    */     {
/*    */       int currentKey;
/* 80 */       if ((currentKey = this.keyTable[i]) != 0)
/* 81 */         newHashtable.put(currentKey, this.valueTable[i]);
/* 79 */       i--; } while (i >= 0);
/*    */ 
/* 83 */     this.keyTable = newHashtable.keyTable;
/* 84 */     this.valueTable = newHashtable.valueTable;
/* 85 */     this.threshold = newHashtable.threshold;
/*    */   }
/*    */   public int size() {
/* 88 */     return this.elementSize;
/*    */   }
/*    */   public String toString() {
/* 91 */     String s = "";
/*    */ 
/* 93 */     int i = 0; for (int length = this.valueTable.length; i < length; i++)
/*    */     {
/*    */       Object object;
/* 94 */       if ((object = this.valueTable[i]) != null)
/* 95 */         s = s + this.keyTable[i] + " -> " + object.toString() + "\n"; 
/*    */     }
/* 96 */     return s;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.HashtableOfInt
 * JD-Core Version:    0.6.0
 */