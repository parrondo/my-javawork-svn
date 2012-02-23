/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class AdminMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "admin";
/*    */ 
/*    */   public AdminMessage()
/*    */   {
/* 25 */     setType("admin");
/*    */   }
/*    */ 
/*    */   public AdminMessage(String name)
/*    */   {
/* 33 */     setType("admin");
/* 34 */     setName(name);
/*    */   }
/*    */ 
/*    */   public AdminMessage(ProtocolMessage message)
/*    */   {
/* 43 */     super(message);
/* 44 */     setType("admin");
/* 45 */     put("name", message.getString("name"));
/* 46 */     put("reloadUserId", message.getString("reloadUserId"));
/*    */   }
/*    */ 
/*    */   public void setName(String name)
/*    */   {
/* 54 */     put("name", name);
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 63 */     return getString("name");
/*    */   }
/*    */ 
/*    */   public void setReloadUserId(String userId)
/*    */   {
/* 71 */     put("reloadUserId", userId);
/*    */   }
/*    */ 
/*    */   public String getReloadUserId()
/*    */   {
/* 80 */     return getString("reloadUserId");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.AdminMessage
 * JD-Core Version:    0.6.0
 */