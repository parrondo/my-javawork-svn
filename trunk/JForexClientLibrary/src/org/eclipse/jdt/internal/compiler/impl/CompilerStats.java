/*    */ package org.eclipse.jdt.internal.compiler.impl;
/*    */ 
/*    */ public class CompilerStats
/*    */   implements Comparable
/*    */ {
/*    */   public long startTime;
/*    */   public long endTime;
/*    */   public long lineCount;
/*    */   public long parseTime;
/*    */   public long resolveTime;
/*    */   public long analyzeTime;
/*    */   public long generateTime;
/*    */ 
/*    */   public long elapsedTime()
/*    */   {
/* 31 */     return this.endTime - this.startTime;
/*    */   }
/*    */ 
/*    */   public int compareTo(Object o)
/*    */   {
/* 38 */     CompilerStats otherStats = (CompilerStats)o;
/* 39 */     long time1 = elapsedTime();
/* 40 */     long time2 = otherStats.elapsedTime();
/* 41 */     return time1 == time2 ? 0 : time1 < time2 ? -1 : 1;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.CompilerStats
 * JD-Core Version:    0.6.0
 */