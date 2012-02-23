/*    */ package com.dukascopy.transport.common.msg.response;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class CurvesWorkspaceResponseMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "workspace_response";
/*    */   public static final String WORKSPACE_NAME = "workspace_name";
/*    */   public static final String WORKSPACE_LIST = "workspace_list";
/*    */   public static final String WORKSPACE_DATA = "workspace_data";
/*    */ 
/*    */   public CurvesWorkspaceResponseMessage()
/*    */   {
/* 18 */     setType("workspace_response");
/*    */   }
/*    */ 
/*    */   public CurvesWorkspaceResponseMessage(ProtocolMessage message) {
/* 22 */     super(message);
/* 23 */     setType("workspace_response");
/* 24 */     setWorkspaceList(message.getJSONArray("workspace_list"));
/* 25 */     setWorkspaceData(message.getString("workspace_data"));
/* 26 */     setWorkspaceName(message.getString("workspace_name"));
/*    */   }
/*    */ 
/*    */   public JSONArray getWorkspaceList() {
/* 30 */     return getJSONArray("workspace_list");
/*    */   }
/*    */ 
/*    */   public void setWorkspaceList(JSONArray workspaceList) {
/* 34 */     put("workspace_list", workspaceList);
/*    */   }
/*    */ 
/*    */   public String getWorkspaceData() {
/* 38 */     return getString("workspace_data");
/*    */   }
/*    */ 
/*    */   public void setWorkspaceData(String workspaceData) {
/* 42 */     put("workspace_data", workspaceData);
/*    */   }
/*    */ 
/*    */   public String getWorkspaceName() {
/* 46 */     return getString("workspace_name");
/*    */   }
/*    */ 
/*    */   public void setWorkspaceName(String workspaceName) {
/* 50 */     put("workspace_name", workspaceName);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.CurvesWorkspaceResponseMessage
 * JD-Core Version:    0.6.0
 */