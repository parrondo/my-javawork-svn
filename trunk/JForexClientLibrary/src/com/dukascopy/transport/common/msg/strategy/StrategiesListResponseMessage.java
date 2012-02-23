/*    */ package com.dukascopy.transport.common.msg.strategy;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class StrategiesListResponseMessage extends ResponseMessage
/*    */   implements IStrategyMessage
/*    */ {
/*    */   public static final String TYPE = "strategies_list_resp";
/*    */   private static final String STRATEGIES = "strategies";
/*    */ 
/*    */   public StrategiesListResponseMessage()
/*    */   {
/* 22 */     setType("strategies_list_resp");
/*    */   }
/*    */ 
/*    */   public StrategiesListResponseMessage(ProtocolMessage msg) {
/* 26 */     super(msg);
/* 27 */     setType("strategies_list_resp");
/*    */ 
/* 29 */     setAccountName(msg.getString("account_name"));
/* 30 */     put("strategies", msg.getJSONArray("strategies"));
/*    */   }
/*    */ 
/*    */   public void setAccountName(String accountName) {
/* 34 */     if ((accountName != null) && (!accountName.trim().isEmpty()))
/* 35 */       put("account_name", accountName);
/*    */   }
/*    */ 
/*    */   public String getAccountName()
/*    */   {
/* 40 */     return getString("account_name");
/*    */   }
/*    */ 
/*    */   public Collection<StrategyProcessDescriptor> getStrategies() {
/* 44 */     Collection result = new ArrayList();
/* 45 */     JSONArray array = getJSONArray("strategies");
/* 46 */     if (array != null) {
/* 47 */       for (int i = 0; i < array.length(); i++) {
/* 48 */         result.add(new StrategyProcessDescriptor(array.getJSONObject(i)));
/*    */       }
/*    */     }
/* 51 */     return result;
/*    */   }
/*    */ 
/*    */   public void setStrategies(Collection<StrategyProcessDescriptor> strategies) {
/* 55 */     put("strategies", new JSONArray(strategies));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.strategy.StrategiesListResponseMessage
 * JD-Core Version:    0.6.0
 */