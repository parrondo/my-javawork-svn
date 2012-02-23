/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.text.ParseException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class ExecutorAccountsMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "execut";
/*    */   public static final String EXECUTORS = "exelist";
/*    */   public static final String EXECUTOR_TYPE = "exectype";
/*    */   public static final String EXEC_TYPE_FUND = "type_fund";
/*    */   public static final String EXEC_TYPE_SYSTEM = "type_system";
/*    */ 
/*    */   public ExecutorAccountsMessage()
/*    */   {
/* 30 */     setType("execut");
/*    */   }
/*    */ 
/*    */   public ExecutorAccountsMessage(ProtocolMessage message)
/*    */   {
/* 39 */     super(message);
/* 40 */     setType("execut");
/* 41 */     put("exectype", message.getString("exectype"));
/* 42 */     put("exelist", message.getJSONArray("exelist"));
/*    */   }
/*    */ 
/*    */   public String getExecutorType()
/*    */   {
/* 51 */     return getString("exectype");
/*    */   }
/*    */ 
/*    */   public void setExecutorType(String type)
/*    */   {
/* 60 */     put("exectype", type);
/*    */   }
/*    */ 
/*    */   public List<ExecutorAccountInfoMessage> getExcutorss()
/*    */     throws ParseException
/*    */   {
/* 70 */     JSONArray a = getJSONArray("exelist");
/*    */ 
/* 72 */     if (a == null) {
/* 73 */       return new ArrayList(0);
/*    */     }
/* 75 */     List set = new ArrayList(a.length());
/* 76 */     for (int i = 0; i < a.length(); i++) {
/* 77 */       ExecutorAccountInfoMessage client = (ExecutorAccountInfoMessage)ProtocolMessage.parse(a.getString(i));
/* 78 */       set.add(client);
/*    */     }
/* 80 */     return set;
/*    */   }
/*    */ 
/*    */   public void setClients(List<ExecutorAccountInfoMessage> executors)
/*    */   {
/* 89 */     JSONArray a = new JSONArray();
/* 90 */     for (ExecutorAccountInfoMessage client : executors) {
/* 91 */       a.put(client);
/*    */     }
/* 93 */     put("exelist", a);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ExecutorAccountsMessage
 * JD-Core Version:    0.6.0
 */