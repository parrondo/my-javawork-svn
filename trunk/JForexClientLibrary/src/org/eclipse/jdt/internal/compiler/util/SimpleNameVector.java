/*    */ package org.eclipse.jdt.internal.compiler.util;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public final class SimpleNameVector
/*    */ {
/* 17 */   static int INITIAL_SIZE = 10;
/*    */   public int size;
/*    */   int maxSize;
/*    */   char[][] elements;
/*    */ 
/*    */   public SimpleNameVector()
/*    */   {
/* 25 */     this.maxSize = INITIAL_SIZE;
/* 26 */     this.size = 0;
/* 27 */     this.elements = new char[this.maxSize][];
/*    */   }
/*    */ 
/*    */   public void add(char[] newElement)
/*    */   {
/* 32 */     if (this.size == this.maxSize)
/* 33 */       System.arraycopy(this.elements, 0, this.elements = new char[this.maxSize *= 2][], 0, this.size);
/* 34 */     this.elements[(this.size++)] = newElement;
/*    */   }
/*    */ 
/*    */   public void addAll(char[][] newElements)
/*    */   {
/* 39 */     if (this.size + newElements.length >= this.maxSize) {
/* 40 */       this.maxSize = (this.size + newElements.length);
/* 41 */       System.arraycopy(this.elements, 0, this.elements = new char[this.maxSize][], 0, this.size);
/*    */     }
/* 43 */     System.arraycopy(newElements, 0, this.elements, this.size, newElements.length);
/* 44 */     this.size += newElements.length;
/*    */   }
/*    */ 
/*    */   public void copyInto(Object[] targetArray)
/*    */   {
/* 49 */     System.arraycopy(this.elements, 0, targetArray, 0, this.size);
/*    */   }
/*    */ 
/*    */   public boolean contains(char[] element)
/*    */   {
/* 54 */     int i = this.size;
/*    */     do { if (CharOperation.equals(element, this.elements[i]))
/* 56 */         return true;
/* 54 */       i--; } while (i >= 0);
/*    */ 
/* 57 */     return false;
/*    */   }
/*    */ 
/*    */   public char[] elementAt(int index) {
/* 61 */     return this.elements[index];
/*    */   }
/*    */ 
/*    */   public char[] remove(char[] element)
/*    */   {
/* 67 */     int i = this.size;
/*    */     do { if (element == this.elements[i])
/*    */       {
/* 70 */         System.arraycopy(this.elements, i + 1, this.elements, i, --this.size - i);
/* 71 */         this.elements[this.size] = null;
/* 72 */         return element;
/*    */       }
/* 67 */       i--; } while (i >= 0);
/*    */ 
/* 74 */     return null;
/*    */   }
/*    */ 
/*    */   public void removeAll()
/*    */   {
/* 79 */     int i = this.size;
/*    */     do { this.elements[i] = null;
/*    */ 
/* 79 */       i--; } while (i >= 0);
/*    */ 
/* 81 */     this.size = 0;
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 86 */     return this.size;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 90 */     StringBuffer buffer = new StringBuffer();
/* 91 */     for (int i = 0; i < this.size; i++) {
/* 92 */       buffer.append(this.elements[i]).append("\n");
/*    */     }
/* 94 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.SimpleNameVector
 * JD-Core Version:    0.6.0
 */