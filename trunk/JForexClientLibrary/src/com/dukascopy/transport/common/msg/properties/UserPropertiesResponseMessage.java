/*    */ package com.dukascopy.transport.common.msg.properties;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.Properties;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class UserPropertiesResponseMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "user.properties.response";
/*    */   private static final String USER_PROPERTIES = "user.properties";
/*    */ 
/*    */   public UserPropertiesResponseMessage()
/*    */   {
/* 20 */     setType("user.properties.response");
/*    */   }
/*    */ 
/*    */   public UserPropertiesResponseMessage(ProtocolMessage message) {
/* 24 */     super(message);
/* 25 */     setType("user.properties.response");
/* 26 */     put("user.properties", message.get("user.properties"));
/*    */   }
/*    */ 
/*    */   public void setUserProperties(Properties properties) {
/* 30 */     if (properties == null) {
/* 31 */       throw new NullPointerException("User properties");
/*    */     }
/*    */ 
/* 34 */     JSONArray jsonArray = new JSONArray();
/* 35 */     for (String propertyName : properties.stringPropertyNames()) {
/* 36 */       jsonArray.put(new UserProperty(propertyName, properties.getProperty(propertyName)));
/*    */     }
/* 38 */     put("user.properties", jsonArray);
/*    */   }
/*    */ 
/*    */   public Properties getUserProperties() {
/* 42 */     Properties result = new Properties();
/* 43 */     JSONArray jsonArray = getJSONArray("user.properties");
/* 44 */     for (int i = 0; i < jsonArray.length(); i++) {
/* 45 */       UserProperty userProperty = new UserProperty(jsonArray.getJSONObject(i));
/* 46 */       result.setProperty(userProperty.getName(), userProperty.getValue());
/*    */     }
/* 48 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.properties.UserPropertiesResponseMessage
 * JD-Core Version:    0.6.0
 */