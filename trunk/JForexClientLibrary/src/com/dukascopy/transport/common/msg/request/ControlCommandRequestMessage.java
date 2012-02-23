/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.ServiceCommand;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class ControlCommandRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "srv_command";
/*    */ 
/*    */   public ControlCommandRequestMessage()
/*    */   {
/* 23 */     setType("srv_command");
/*    */   }
/*    */ 
/*    */   public ControlCommandRequestMessage(ProtocolMessage message)
/*    */   {
/* 33 */     super(message);
/* 34 */     setType("srv_command");
/* 35 */     put("srvcommand", message.getString("srvcommand"));
/* 36 */     put("params", message.getJSONArray("params"));
/* 37 */     put("serviceName", message.getString("serviceName"));
/*    */   }
/*    */ 
/*    */   public ServiceCommand getCommand() {
/* 41 */     return ServiceCommand.fromString(getString("srvcommand"));
/*    */   }
/*    */ 
/*    */   public void setCommand(ServiceCommand command) {
/* 45 */     put("srvcommand", command);
/*    */   }
/*    */ 
/*    */   public String[] getCommandParams() {
/* 49 */     String[] res = new String[0];
/*    */ 
/* 51 */     JSONArray array = getJSONArray("params");
/* 52 */     if (array != null) {
/* 53 */       res = new String[array.length()];
/* 54 */       for (int i = 0; i < array.length(); i++) {
/* 55 */         res[i] = array.getString(i);
/*    */       }
/*    */     }
/* 58 */     return res;
/*    */   }
/*    */ 
/*    */   public void setCommandParams(String[] params) {
/* 62 */     put("params", new JSONArray());
/* 63 */     JSONArray array = getJSONArray("params");
/* 64 */     if (params != null)
/*    */     {
/* 66 */       for (int i = 0; i < params.length; i++)
/* 67 */         array.put(params[i]);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setServiceName(String serviceName)
/*    */   {
/* 73 */     put("serviceName", serviceName);
/*    */   }
/*    */ 
/*    */   public String getServiceName(String serviveName) {
/* 77 */     return getString("serviceName");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ControlCommandRequestMessage
 * JD-Core Version:    0.6.0
 */