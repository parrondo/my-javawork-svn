/*     */ package org.eclipse.jdt.internal.compiler.batch;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ class Main$1
/*     */   implements Comparator
/*     */ {
/*     */   final Main.Logger this$1;
/*     */ 
/*     */   public int compare(Object o1, Object o2)
/*     */   {
/* 694 */     Map.Entry entry1 = (Map.Entry)o1;
/* 695 */     Map.Entry entry2 = (Map.Entry)o2;
/* 696 */     return ((String)entry1.getKey()).compareTo((String)entry2.getKey());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.batch.Main.1
 * JD-Core Version:    0.6.0
 */