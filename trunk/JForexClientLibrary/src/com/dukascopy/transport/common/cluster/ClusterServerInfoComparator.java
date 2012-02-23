/*    */ package com.dukascopy.transport.common.cluster;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ 
/*    */ public class ClusterServerInfoComparator
/*    */   implements Comparator
/*    */ {
/*    */   public long totalServerQuotient;
/*    */   public int totalTaskPoolSize;
/*    */ 
/*    */   public int compare(Object csi1, Object csi2)
/*    */   {
/* 11 */     long serverQuotient1 = ((ClusterServerInfo)csi1).getServerQuotient();
/* 12 */     int taskPoolSize1 = ((ClusterServerInfo)csi1).getTaskPoolSize();
/* 13 */     long serverQuotient2 = ((ClusterServerInfo)csi2).getServerQuotient();
/* 14 */     int taskPoolSize2 = ((ClusterServerInfo)csi2).getTaskPoolSize();
/* 15 */     long weight1 = calcWeight(serverQuotient1, taskPoolSize1, this.totalServerQuotient, this.totalTaskPoolSize);
/* 16 */     long weight2 = calcWeight(serverQuotient2, taskPoolSize2, this.totalServerQuotient, this.totalTaskPoolSize);
/*    */ 
/* 18 */     return weight1 > weight2 ? 1 : -1;
/*    */   }
/*    */ 
/*    */   public long getTotalServerQuotient() {
/* 22 */     return this.totalServerQuotient;
/*    */   }
/*    */ 
/*    */   public void setTotalServerQuotient(long totalServerQuotient) {
/* 26 */     this.totalServerQuotient = totalServerQuotient;
/*    */   }
/*    */ 
/*    */   public int getTotalTaskPoolSize() {
/* 30 */     return this.totalTaskPoolSize;
/*    */   }
/*    */ 
/*    */   public void setTotalTaskPoolSize(int totalTaskPoolSize) {
/* 34 */     this.totalTaskPoolSize = totalTaskPoolSize;
/*    */   }
/*    */ 
/*    */   public static long calcWeight(long serverQuotient, int taskPoolSize, long totalServerQuotient, int totalTaskPoolSize)
/*    */   {
/* 48 */     return taskPoolSize - serverQuotient;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.cluster.ClusterServerInfoComparator
 * JD-Core Version:    0.6.0
 */