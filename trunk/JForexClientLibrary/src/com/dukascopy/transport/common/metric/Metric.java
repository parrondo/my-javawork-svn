/*    */ package com.dukascopy.transport.common.metric;
/*    */ 
/*    */ public class Metric
/*    */ {
/*    */   private String group;
/*    */ 
/*    */   public void setGroup(String group)
/*    */   {
/* 19 */     this.group = group;
/*    */   }
/*    */ 
/*    */   public String getGroup() {
/* 23 */     return this.group;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 27 */     StringBuffer sb = new StringBuffer("Metric{");
/* 28 */     sb.append("group=").append(getGroup());
/* 29 */     sb.append("}");
/* 30 */     return sb.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.metric.Metric
 * JD-Core Version:    0.6.0
 */