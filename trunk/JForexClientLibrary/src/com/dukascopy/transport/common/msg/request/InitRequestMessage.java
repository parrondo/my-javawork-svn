/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class InitRequestMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "init";
/*    */   public static final String USERIDLIST = "userIdList";
/*    */   public static final String SEND_GROUPS = "sgr";
/*    */ 
/*    */   public InitRequestMessage()
/*    */   {
/* 27 */     setType("init");
/*    */   }
/*    */ 
/*    */   public InitRequestMessage(ProtocolMessage message)
/*    */   {
/* 36 */     super(message);
/* 37 */     setType("init");
/* 38 */     put("userIdList", message.getJSONArray("userIdList"));
/* 39 */     put("sgr", message.getString("sgr"));
/*    */   }
/*    */ 
/*    */   public List<String> getUserIdList() {
/* 43 */     JSONArray a = getJSONArray("userIdList");
/* 44 */     List userIdList = new ArrayList();
/* 45 */     if (a != null) {
/* 46 */       for (int i = 0; i < a.length(); i++) {
/* 47 */         userIdList.add((String)a.get(i));
/*    */       }
/*    */     }
/* 50 */     return userIdList;
/*    */   }
/*    */ 
/*    */   public void setUserIdList(List<String> userIdList) {
/* 54 */     put("userIdList", new JSONArray(userIdList));
/*    */   }
/*    */ 
/*    */   public boolean isSendGroups()
/*    */   {
/* 64 */     Boolean result = Boolean.valueOf(true);
/* 65 */     if (!has("sgr")) {
/* 66 */       return true;
/*    */     }
/* 68 */     String sres = getString("sgr");
/* 69 */     if (sres != null) {
/* 70 */       result = new Boolean(sres);
/*    */     }
/*    */ 
/* 73 */     return result.booleanValue();
/*    */   }
/*    */ 
/*    */   public void setSendGroups(boolean sg)
/*    */   {
/* 82 */     put("sgr", sg + "");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.InitRequestMessage
 * JD-Core Version:    0.6.0
 */