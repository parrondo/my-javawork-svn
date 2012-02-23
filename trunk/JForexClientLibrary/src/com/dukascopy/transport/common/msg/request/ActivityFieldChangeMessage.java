/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.text.ParseException;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class ActivityFieldChangeMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "afcm";
/*    */   public static final String PARAM_KEY = "paramKey";
/*    */   public static final String OLD_VALUE = "oldVal";
/*    */   public static final String NEW_VALUE = "newVal";
/*    */ 
/*    */   public ActivityFieldChangeMessage()
/*    */   {
/* 23 */     setType("afcm");
/*    */   }
/*    */ 
/*    */   public ActivityFieldChangeMessage(String json) throws ParseException {
/* 27 */     super(json);
/* 28 */     setType("afcm");
/*    */   }
/*    */ 
/*    */   public ActivityFieldChangeMessage(JSONObject json) throws ParseException {
/* 32 */     super(json);
/* 33 */     setType("afcm");
/*    */   }
/*    */ 
/*    */   public ActivityFieldChangeMessage(String paramKey, String oldVal, String newVal)
/*    */   {
/* 38 */     setType("afcm");
/* 39 */     setParamKey(paramKey);
/* 40 */     setOldVal(oldVal);
/* 41 */     setNewVal(newVal);
/*    */   }
/*    */ 
/*    */   public ActivityFieldChangeMessage(ProtocolMessage msg) {
/* 45 */     super(msg);
/* 46 */     setType("afcm");
/* 47 */     setParamKey(msg.getString("paramKey"));
/* 48 */     setOldVal(msg.getString("oldVal"));
/* 49 */     setNewVal(msg.getString("newVal"));
/*    */   }
/*    */ 
/*    */   public String getParamKey() {
/* 53 */     return getString("paramKey");
/*    */   }
/*    */ 
/*    */   public void setParamKey(String paramKey) {
/* 57 */     put("paramKey", paramKey);
/*    */   }
/*    */ 
/*    */   public String getOldVal() {
/* 61 */     return getString("oldVal");
/*    */   }
/*    */ 
/*    */   public void setOldVal(String oldVal) {
/* 65 */     put("oldVal", oldVal);
/*    */   }
/*    */ 
/*    */   public String getNewVal() {
/* 69 */     return getString("newVal");
/*    */   }
/*    */ 
/*    */   public void setNewVal(String newVal) {
/* 73 */     put("newVal", newVal);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ActivityFieldChangeMessage
 * JD-Core Version:    0.6.0
 */