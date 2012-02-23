/*    */ package org.eclipse.jdt.internal.compiler.util;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public final class CompoundNameVector
/*    */ {
/* 16 */   static int INITIAL_SIZE = 10;
/*    */   public int size;
/*    */   int maxSize;
/*    */   char[][][] elements;
/*    */ 
/*    */   public CompoundNameVector()
/*    */   {
/* 22 */     this.maxSize = INITIAL_SIZE;
/* 23 */     this.size = 0;
/* 24 */     this.elements = new char[this.maxSize][][];
/*    */   }
/*    */   public void add(char[][] newElement) {
/* 27 */     if (this.size == this.maxSize)
/* 28 */       System.arraycopy(this.elements, 0, this.elements = new char[this.maxSize *= 2][][], 0, this.size);
/* 29 */     this.elements[(this.size++)] = newElement;
/*    */   }
/*    */   public void addAll(char[][][] newElements) {
/* 32 */     if (this.size + newElements.length >= this.maxSize) {
/* 33 */       this.maxSize = (this.size + newElements.length);
/* 34 */       System.arraycopy(this.elements, 0, this.elements = new char[this.maxSize][][], 0, this.size);
/*    */     }
/* 36 */     System.arraycopy(newElements, 0, this.elements, this.size, newElements.length);
/* 37 */     this.size += newElements.length;
/*    */   }
/*    */   public boolean contains(char[][] element) {
/* 40 */     int i = this.size;
/*    */     do { if (CharOperation.equals(element, this.elements[i]))
/* 42 */         return true;
/* 40 */       i--; } while (i >= 0);
/*    */ 
/* 43 */     return false;
/*    */   }
/*    */   public char[][] elementAt(int index) {
/* 46 */     return this.elements[index];
/*    */   }
/*    */ 
/*    */   public char[][] remove(char[][] element) {
/* 50 */     int i = this.size;
/*    */     do { if (element == this.elements[i])
/*    */       {
/* 53 */         System.arraycopy(this.elements, i + 1, this.elements, i, --this.size - i);
/* 54 */         this.elements[this.size] = null;
/* 55 */         return element;
/*    */       }
/* 50 */       i--; } while (i >= 0);
/*    */ 
/* 57 */     return null;
/*    */   }
/*    */   public void removeAll() {
/* 60 */     int i = this.size;
/*    */     do { this.elements[i] = null;
/*    */ 
/* 60 */       i--; } while (i >= 0);
/*    */ 
/* 62 */     this.size = 0;
/*    */   }
/*    */   public String toString() {
/* 65 */     StringBuffer buffer = new StringBuffer();
/* 66 */     for (int i = 0; i < this.size; i++) {
/* 67 */       buffer.append(CharOperation.toString(this.elements[i])).append("\n");
/*    */     }
/* 69 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.CompoundNameVector
 * JD-Core Version:    0.6.0
 */