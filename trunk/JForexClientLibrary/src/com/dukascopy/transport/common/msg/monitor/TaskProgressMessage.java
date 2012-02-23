/*    */ package com.dukascopy.transport.common.msg.monitor;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class TaskProgressMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "task_prog";
/*    */   private static final String TASK_ID = "task_id";
/*    */   private static final String MAX_DURATION = "max";
/*    */   private static final String PROGRESS = "progress";
/*    */   private static final String STATUS = "status";
/*    */   private static final String TEXT = "text";
/*    */ 
/*    */   public TaskProgressMessage(String taskId)
/*    */   {
/* 17 */     setType("task_prog");
/* 18 */     setTaskId(taskId);
/*    */   }
/*    */ 
/*    */   public TaskProgressMessage(ProtocolMessage msg) {
/* 22 */     super(msg);
/* 23 */     setType("task_prog");
/* 24 */     setTaskId(msg.getString("task_id"));
/* 25 */     setMaxDuration(msg.getInteger("max"));
/* 26 */     setProgress(msg.getInteger("progress"));
/* 27 */     put("status", msg.getString("status"));
/* 28 */     setText(msg.getString("text"));
/*    */   }
/*    */ 
/*    */   public String getTaskId() {
/* 32 */     return getString("task_id");
/*    */   }
/*    */ 
/*    */   public void setTaskId(String taskId) {
/* 36 */     put("task_id", taskId);
/*    */   }
/*    */ 
/*    */   public void setMaxDuration(Integer maxDuration) {
/* 40 */     put("max", maxDuration);
/*    */   }
/*    */ 
/*    */   public Integer getMaxDuration() {
/* 44 */     return getInteger("max");
/*    */   }
/*    */ 
/*    */   public void setProgress(Integer progress) {
/* 48 */     put("progress", progress);
/*    */   }
/*    */ 
/*    */   public Integer getProgress() {
/* 52 */     return getInteger("progress");
/*    */   }
/*    */ 
/*    */   public TaskStatus getStatus() {
/* 56 */     String status = getString("status");
/* 57 */     if (status != null) {
/* 58 */       return TaskStatus.valueOf(status);
/*    */     }
/* 60 */     return null;
/*    */   }
/*    */ 
/*    */   public void setStatus(TaskStatus status) {
/* 64 */     if (status != null)
/* 65 */       put("status", status.toString());
/*    */   }
/*    */ 
/*    */   public String getText()
/*    */   {
/* 70 */     return getString("text");
/*    */   }
/*    */ 
/*    */   public void setText(String text) {
/* 74 */     put("text", text);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.monitor.TaskProgressMessage
 * JD-Core Version:    0.6.0
 */