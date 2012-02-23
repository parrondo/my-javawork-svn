/*    */ package com.dukascopy.transport.common.msg.properties;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.util.Properties;
/*    */ import org.json.JSONArray;
/*    */ 
/*    */ public class UserPropertiesChangeMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "user.properties.change";
/*    */   private static final String USER_PROPERTIES = "user.properties";
/*    */ 
/*    */   public UserPropertiesChangeMessage()
/*    */   {
/* 20 */     setType("user.properties.change");
/*    */   }
/*    */ 
/*    */   public UserPropertiesChangeMessage(ProtocolMessage message) {
/* 24 */     super(message);
/* 25 */     setType("user.properties.change");
/* 26 */     put("user.properties", message.get("user.properties"));
/*    */   }
/*    */ 
/*    */   public void setUserProperties(Properties properties) {
/* 30 */     if (properties == null) {
/* 31 */       throw new NullPointerException("User properties");
/*    */     }
/* 33 */     if (properties.isEmpty()) {
/* 34 */       throw new IllegalArgumentException("User properties is empty");
/*    */     }
/*    */ 
/* 37 */     JSONArray jsonArray = new JSONArray();
/* 38 */     for (String propertyName : properties.stringPropertyNames()) {
/* 39 */       jsonArray.put(new UserProperty(propertyName, properties.getProperty(propertyName)));
/*    */     }
/* 41 */     put("user.properties", jsonArray);
/*    */   }
/*    */ 
/*    */   public Properties getUserProperties() {
/* 45 */     Properties result = new Properties();
/* 46 */     JSONArray jsonArray = getJSONArray("user.properties");
/* 47 */     for (int i = 0; i < jsonArray.length(); i++) {
/* 48 */       UserProperty userProperty = new UserProperty(jsonArray.getJSONObject(i));
/* 49 */       result.setProperty(userProperty.getName(), userProperty.getValue());
/*    */     }
/* 51 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.properties.UserPropertiesChangeMessage
 * JD-Core Version:    0.6.0
 */