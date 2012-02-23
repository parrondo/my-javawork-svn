/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class StrategyUpdateRequestMessage extends RequestMessage
/*    */   implements IStrategyMessage
/*    */ {
/*    */   public static final String TYPE = "strategy_update";
/*    */   private static final String PID = "pid";
/*    */   private static final String FILE_ID = "file_id";
/*    */   private static final String PARAMETERS = "parameters";
/*    */ 
/*    */   public StrategyUpdateRequestMessage()
/*    */   {
/* 24 */     setType("strategy_update");
/*    */   }
/*    */ 
/*    */   public StrategyUpdateRequestMessage(ProtocolMessage msg) {
/* 28 */     super(msg);
/* 29 */     setType("strategy_update");
/*    */ 
/* 31 */     setAccountName(msg.getString("account_name"));
/* 32 */     setPid(msg.getString("pid"));
/* 33 */     put("file_id", msg.get("file_id"));
/* 34 */     put("parameters", msg.getJSONArray("parameters"));
/*    */   }
/*    */ 
/*    */   public void setAccountName(String accountName) {
/* 38 */     if ((accountName != null) && (!accountName.trim().isEmpty()))
/* 39 */       put("account_name", accountName);
/*    */   }
/*    */ 
/*    */   public String getAccountName()
/*    */   {
/* 44 */     return getString("account_name");
/*    */   }
/*    */ 
/*    */   public void setPid(String pid) {
/* 48 */     if ((pid == null) || (pid.isEmpty())) {
/* 49 */       throw new IllegalArgumentException("PID is empty");
/*    */     }
/* 51 */     put("pid", pid);
/*    */   }
/*    */ 
/*    */   public String getPid() {
/* 55 */     String value = getString("pid");
/* 56 */     if ((value == null) || (value.isEmpty())) {
/* 57 */       throw new IllegalArgumentException("PID is empty");
/*    */     }
/* 59 */     return value;
/*    */   }
/*    */ 
/*    */   public void setFileId(Long fileId) {
/* 63 */     if (fileId != null)
/* 64 */       put("file_id", fileId.toString());
/*    */   }
/*    */ 
/*    */   public Long getFileId()
/*    */   {
/* 69 */     return getLong("file_id");
/*    */   }
/*    */ 
/*    */   public Collection<StrategyParameter> getParameters() {
/* 73 */     Collection parameters = new ArrayList();
/* 74 */     if (has("parameters")) {
/* 75 */       JSONArray array = getJSONArray("parameters");
/* 76 */       if (array != null) {
/* 77 */         for (int i = 0; i < array.length(); i++) {
/* 78 */           parameters.add(new StrategyParameter(array.getJSONObject(i)));
/*    */         }
/*    */       }
/*    */     }
/* 82 */     return parameters;
/*    */   }
/*    */ 
/*    */   public void setParameters(Collection<StrategyParameter> parameters) {
/* 86 */     put("parameters", new JSONArray(parameters));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategyUpdateRequestMessage
 * JD-Core Version:    0.6.0
 */