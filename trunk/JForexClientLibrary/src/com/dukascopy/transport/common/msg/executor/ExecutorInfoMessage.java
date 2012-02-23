/*    */ package com.dukascopy.transport.common.msg.executor;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class ExecutorInfoMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "execInfo";
/*    */   private static final String EXECUTOR = "exec_id";
/*    */   private static final String STATUS = "state";
/*    */   private static final String INSTRUMENT_STATES = "istates";
/*    */   private static final String VERSION = "version";
/*    */ 
/*    */   public ExecutorInfoMessage()
/*    */   {
/* 25 */     setType("execInfo");
/*    */   }
/*    */ 
/*    */   public ExecutorInfoMessage(ProtocolMessage message) {
/* 29 */     super(message);
/* 30 */     setType("execInfo");
/* 31 */     put("exec_id", message.getString("exec_id"));
/* 32 */     put("state", message.getString("state"));
/* 33 */     put("istates", message.getJSONObject("istates"));
/* 34 */     put("version", message.getString("version"));
/*    */   }
/*    */ 
/*    */   public void setExecutorId(String executorId) {
/* 38 */     put("exec_id", executorId);
/*    */   }
/*    */ 
/*    */   public String getExecutorId() {
/* 42 */     return getString("exec_id");
/*    */   }
/*    */ 
/*    */   public void setStatus(ExecutorStatus status) {
/* 46 */     if (status != null)
/* 47 */       put("state", status.toString());
/*    */   }
/*    */ 
/*    */   public ExecutorStatus getStatus()
/*    */   {
/* 52 */     String str = getString("state");
/* 53 */     if (str != null) {
/* 54 */       return ExecutorStatus.valueOf(str);
/*    */     }
/* 56 */     return null;
/*    */   }
/*    */ 
/*    */   public void setInstrumentStates(Map<String, Boolean> instrumentStates) {
/* 60 */     if (instrumentStates != null) {
/* 61 */       JSONObject json = new JSONObject();
/* 62 */       synchronized (instrumentStates) {
/* 63 */         for (String instrument : instrumentStates.keySet()) {
/* 64 */           json.put(instrument, instrumentStates.get(instrument));
/*    */         }
/*    */       }
/* 67 */       put("istates", json);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Map<String, Boolean> getInstrumentStates() {
/* 72 */     JSONObject json = getJSONObject("istates");
/* 73 */     Map instrumentStates = new HashMap();
/* 74 */     if (json != null) {
/* 75 */       Iterator instruments = json.keys();
/* 76 */       while (instruments.hasNext()) {
/* 77 */         String instrument = (String)instruments.next();
/* 78 */         instrumentStates.put(instrument, Boolean.valueOf(json.getBoolean(instrument)));
/*    */       }
/*    */     }
/* 81 */     return instrumentStates;
/*    */   }
/*    */ 
/*    */   public void setVersion(String version) {
/* 85 */     put("version", version);
/*    */   }
/*    */ 
/*    */   public String getVersion() {
/* 89 */     return getString("version");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.executor.ExecutorInfoMessage
 * JD-Core Version:    0.6.0
 */