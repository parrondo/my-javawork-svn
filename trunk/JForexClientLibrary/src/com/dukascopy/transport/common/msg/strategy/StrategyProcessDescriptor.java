/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import org.json.JSONArray;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class StrategyProcessDescriptor extends JSONObject
/*    */ {
/*    */   private static final String PID = "pid";
/*    */   private static final String REQUEST_ID = "request_id";
/*    */   private static final String FILE_ID = "file_id";
/*    */   private static final String FILE_NAME = "file_name";
/*    */   private static final String PARAMETERS = "parameters";
/*    */ 
/*    */   public StrategyProcessDescriptor()
/*    */   {
/*    */   }
/*    */ 
/*    */   public StrategyProcessDescriptor(JSONObject jsonObject)
/*    */   {
/* 25 */     super(jsonObject, new String[] { "pid", "file_id", "file_name", "request_id", "parameters" });
/*    */   }
/*    */ 
/*    */   public String getPid() {
/* 29 */     String value = getString("pid");
/* 30 */     if ((value == null) || (value.isEmpty())) {
/* 31 */       throw new IllegalArgumentException("PID is empty");
/*    */     }
/* 33 */     return value;
/*    */   }
/*    */ 
/*    */   public void setPid(String pid) {
/* 37 */     if ((pid == null) || (pid.isEmpty())) {
/* 38 */       throw new IllegalArgumentException("PID is empty");
/*    */     }
/* 40 */     put("pid", pid);
/*    */   }
/*    */ 
/*    */   public String getRequestId() {
/* 44 */     return getString("request_id");
/*    */   }
/*    */ 
/*    */   public void setRequestId(String requestId) {
/* 48 */     if (requestId != null)
/* 49 */       put("request_id", requestId);
/*    */   }
/*    */ 
/*    */   public Long getFileId()
/*    */   {
/* 54 */     String value = getString("file_id");
/* 55 */     if (value != null) {
/* 56 */       return Long.valueOf(Long.parseLong(value));
/*    */     }
/* 58 */     return null;
/*    */   }
/*    */ 
/*    */   public void setFileId(Long fileId) {
/* 62 */     if (fileId != null)
/* 63 */       put("file_id", String.valueOf(fileId));
/*    */   }
/*    */ 
/*    */   public String getFileName()
/*    */   {
/* 68 */     String value = getString("file_name");
/* 69 */     if ((value == null) || (value.isEmpty())) {
/* 70 */       throw new IllegalArgumentException("File name is empty");
/*    */     }
/* 72 */     return value;
/*    */   }
/*    */ 
/*    */   public void setFileName(String fileName)
/*    */   {
/* 77 */     if ((fileName == null) || (fileName.isEmpty())) {
/* 78 */       throw new IllegalArgumentException("File name is empty");
/*    */     }
/* 80 */     put("file_name", fileName);
/*    */   }
/*    */ 
/*    */   public Collection<StrategyParameter> getParameters() {
/* 84 */     Collection parameters = new ArrayList();
/* 85 */     if (has("parameters")) {
/* 86 */       JSONArray array = getJSONArray("parameters");
/* 87 */       if (array != null) {
/* 88 */         for (int i = 0; i < array.length(); i++) {
/* 89 */           parameters.add(new StrategyParameter(array.getJSONObject(i)));
/*    */         }
/*    */       }
/*    */     }
/* 93 */     return parameters;
/*    */   }
/*    */ 
/*    */   public void setParameters(Collection<StrategyParameter> parameters) {
/* 97 */     put("parameters", new JSONArray(parameters));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyProcessDescriptor
 * JD-Core Version:    0.6.0
 */