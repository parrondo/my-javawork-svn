/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.RequestMessage;
/*    */ 
/*    */ public class VersionInfoMessage extends RequestMessage
/*    */ {
/*    */   public static final String TYPE = "verInfo";
/*    */   public static final String SERVER_VERSION = "srvVer";
/*    */ 
/*    */   public VersionInfoMessage()
/*    */   {
/* 22 */     setType("verInfo");
/*    */   }
/*    */ 
/*    */   public VersionInfoMessage(ProtocolMessage message)
/*    */   {
/* 31 */     super(message);
/*    */ 
/* 33 */     setType("verInfo");
/*    */ 
/* 35 */     setServerVersion(message.getString("srvVer"));
/*    */   }
/*    */ 
/*    */   public VersionInfoMessage(String serverVersion)
/*    */   {
/* 46 */     setType("verInfo");
/*    */ 
/* 48 */     setServerVersion(serverVersion);
/*    */   }
/*    */ 
/*    */   public void setServerVersion(String version)
/*    */   {
/* 57 */     put("srvVer", version);
/*    */   }
/*    */ 
/*    */   public String getServerVersion()
/*    */   {
/* 66 */     return getString("srvVer");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.VersionInfoMessage
 * JD-Core Version:    0.6.0
 */