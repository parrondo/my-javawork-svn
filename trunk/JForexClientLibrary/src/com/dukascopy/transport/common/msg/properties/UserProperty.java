/*    */ package com.dukascopy.transport.common.msg.properties;
/*    */ 
/*    */ import com.dukascopy.transport.util.Base64;
/*    */ import java.nio.charset.Charset;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ class UserProperty extends JSONObject
/*    */ {
/* 14 */   private static final Charset UTF8 = Charset.forName("UTF-8");
/*    */   private static final String NAME = "name";
/*    */   private static final String VALUE = "value";
/*    */ 
/*    */   public UserProperty()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UserProperty(JSONObject jsonObject)
/*    */   {
/* 24 */     super(jsonObject, new String[] { "name", "value" });
/*    */   }
/*    */ 
/*    */   public UserProperty(String name, String value)
/*    */   {
/* 29 */     setName(name);
/* 30 */     setValue(value);
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 34 */     return getString("name");
/*    */   }
/*    */ 
/*    */   public void setName(String name) {
/* 38 */     if (name == null) {
/* 39 */       throw new NullPointerException("name");
/*    */     }
/* 41 */     put("name", name);
/*    */   }
/*    */ 
/*    */   public String getValue() {
/* 45 */     String value = getString("value");
/* 46 */     if ((value != null) && (!value.isEmpty())) {
/* 47 */       return new String(Base64.decode(value), UTF8);
/*    */     }
/* 49 */     return null;
/*    */   }
/*    */ 
/*    */   public void setValue(Object value)
/*    */   {
/* 54 */     if (value != null)
/* 55 */       put("value", Base64.encode(String.valueOf(value).getBytes(UTF8)));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.properties.UserProperty
 * JD-Core Version:    0.6.0
 */