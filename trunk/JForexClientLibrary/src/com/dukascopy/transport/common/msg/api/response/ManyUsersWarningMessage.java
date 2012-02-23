/*    */ package com.dukascopy.transport.common.msg.api.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class ManyUsersWarningMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "manyUsers";
/*    */   public static final String USERIDLIST = "userIdList";
/*    */ 
/*    */   public ManyUsersWarningMessage()
/*    */   {
/* 24 */     setType("manyUsers");
/*    */   }
/*    */ 
/*    */   public ManyUsersWarningMessage(ProtocolMessage message)
/*    */   {
/* 33 */     super(message);
/* 34 */     setType("manyUsers");
/* 35 */     put("userIdList", message.getJSONArray("userIdList"));
/*    */   }
/*    */ 
/*    */   public List<String> getUserIdList() {
/* 39 */     JSONArray a = getJSONArray("userIdList");
/* 40 */     List userIdList = new ArrayList();
/* 41 */     if (a != null) {
/* 42 */       for (int i = 0; i < a.length(); i++) {
/* 43 */         userIdList.add((String)a.get(i));
/*    */       }
/*    */     }
/* 46 */     return userIdList;
/*    */   }
/*    */ 
/*    */   public void setUserIdList(Set<String> userIdList) {
/* 50 */     put("userIdList", new JSONArray(userIdList));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.ManyUsersWarningMessage
 * JD-Core Version:    0.6.0
 */