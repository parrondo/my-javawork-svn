/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ public final class ObjectVector
/*     */ {
/*  15 */   static int INITIAL_SIZE = 10;
/*     */   public int size;
/*     */   int maxSize;
/*     */   Object[] elements;
/*     */ 
/*     */   public ObjectVector()
/*     */   {
/*  22 */     this(INITIAL_SIZE);
/*     */   }
/*     */ 
/*     */   public ObjectVector(int initialSize) {
/*  26 */     this.maxSize = (initialSize > 0 ? initialSize : INITIAL_SIZE);
/*  27 */     this.size = 0;
/*  28 */     this.elements = new Object[this.maxSize];
/*     */   }
/*     */ 
/*     */   public void add(Object newElement)
/*     */   {
/*  33 */     if (this.size == this.maxSize)
/*  34 */       System.arraycopy(this.elements, 0, this.elements = new Object[this.maxSize *= 2], 0, this.size);
/*  35 */     this.elements[(this.size++)] = newElement;
/*     */   }
/*     */ 
/*     */   public void addAll(Object[] newElements)
/*     */   {
/*  40 */     if (this.size + newElements.length >= this.maxSize) {
/*  41 */       this.maxSize = (this.size + newElements.length);
/*  42 */       System.arraycopy(this.elements, 0, this.elements = new Object[this.maxSize], 0, this.size);
/*     */     }
/*  44 */     System.arraycopy(newElements, 0, this.elements, this.size, newElements.length);
/*  45 */     this.size += newElements.length;
/*     */   }
/*     */ 
/*     */   public void addAll(ObjectVector newVector)
/*     */   {
/*  50 */     if (this.size + newVector.size >= this.maxSize) {
/*  51 */       this.maxSize = (this.size + newVector.size);
/*  52 */       System.arraycopy(this.elements, 0, this.elements = new Object[this.maxSize], 0, this.size);
/*     */     }
/*  54 */     System.arraycopy(newVector.elements, 0, this.elements, this.size, newVector.size);
/*  55 */     this.size += newVector.size;
/*     */   }
/*     */ 
/*     */   public boolean containsIdentical(Object element)
/*     */   {
/*  63 */     int i = this.size;
/*     */     do { if (element == this.elements[i])
/*  65 */         return true;
/*  63 */       i--; } while (i >= 0);
/*     */ 
/*  66 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean contains(Object element)
/*     */   {
/*  74 */     int i = this.size;
/*     */     do { if (element.equals(this.elements[i]))
/*  76 */         return true;
/*  74 */       i--; } while (i >= 0);
/*     */ 
/*  77 */     return false;
/*     */   }
/*     */ 
/*     */   public void copyInto(Object[] targetArray)
/*     */   {
/*  82 */     copyInto(targetArray, 0);
/*     */   }
/*     */ 
/*     */   public void copyInto(Object[] targetArray, int index)
/*     */   {
/*  87 */     System.arraycopy(this.elements, 0, targetArray, index, this.size);
/*     */   }
/*     */ 
/*     */   public Object elementAt(int index)
/*     */   {
/*  92 */     return this.elements[index];
/*     */   }
/*     */ 
/*     */   public Object find(Object element)
/*     */   {
/*  97 */     int i = this.size;
/*     */     do { if (element.equals(this.elements[i]))
/*  99 */         return this.elements[i];
/*  97 */       i--; } while (i >= 0);
/*     */ 
/* 100 */     return null;
/*     */   }
/*     */ 
/*     */   public Object remove(Object element)
/*     */   {
/* 106 */     int i = this.size;
/*     */     do { if (element.equals(this.elements[i]))
/*     */       {
/* 109 */         System.arraycopy(this.elements, i + 1, this.elements, i, --this.size - i);
/* 110 */         this.elements[this.size] = null;
/* 111 */         return element;
/*     */       }
/* 106 */       i--; } while (i >= 0);
/*     */ 
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   public void removeAll()
/*     */   {
/* 118 */     int i = this.size;
/*     */     do { this.elements[i] = null;
/*     */ 
/* 118 */       i--; } while (i >= 0);
/*     */ 
/* 120 */     this.size = 0;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 125 */     return this.size;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 130 */     String s = "";
/* 131 */     for (int i = 0; i < this.size; i++)
/* 132 */       s = s + this.elements[i].toString() + "\n";
/* 133 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.ObjectVector
 * JD-Core Version:    0.6.0
 */