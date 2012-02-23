/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ 
/*    */ public class CurvesWorkspaceRequestMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "workspace_request";
/*    */   public static final String USERNAME = "username";
/*    */   public static final String GET_DEFAULT_WORKSPACE = "get_default_workspace";
/*    */   public static final String WORKSPACE_NAME = "workspace_name";
/*    */   public static final String WORKSPACE_DATA = "workspace_data";
/*    */ 
/*    */   public CurvesWorkspaceRequestMessage()
/*    */   {
/* 31 */     setType("workspace_request");
/*    */   }
/*    */ 
/*    */   public CurvesWorkspaceRequestMessage(ProtocolMessage message) {
/* 35 */     super(message);
/* 36 */     setType("workspace_request");
/* 37 */     setUsername(message.getString("username"));
/* 38 */     setGetDefaultWorkspace(message.getBool("get_default_workspace"));
/* 39 */     setWorkspaceName(message.getString("workspace_name"));
/* 40 */     setWorkspaceData(message.getString("workspace_data"));
/*    */   }
/*    */ 
/*    */   public String getUsername() {
/* 44 */     return getString("username");
/*    */   }
/*    */ 
/*    */   public void setUsername(String username) {
/* 48 */     put("username", username);
/*    */   }
/*    */ 
/*    */   public String getWorkspaceName() {
/* 52 */     return getString("workspace_name");
/*    */   }
/*    */ 
/*    */   public void setWorkspaceName(String workspaceName) {
/* 56 */     put("workspace_name", workspaceName);
/*    */   }
/*    */ 
/*    */   public String getWorkspaceData() {
/* 60 */     return getString("workspace_data");
/*    */   }
/*    */ 
/*    */   public void setWorkspaceData(String workspaceData) {
/* 64 */     put("workspace_data", workspaceData);
/*    */   }
/*    */ 
/*    */   public Boolean isGetDefaultWorkspace() {
/* 68 */     return getBool("get_default_workspace");
/*    */   }
/*    */ 
/*    */   public void setGetDefaultWorkspace(Boolean oco) {
/* 72 */     put("get_default_workspace", oco);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.CurvesWorkspaceRequestMessage
 * JD-Core Version:    0.6.0
 */