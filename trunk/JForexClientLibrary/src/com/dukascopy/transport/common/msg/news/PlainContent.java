/*    */ package com.dukascopy.transport.common.msg.news;
/*    */ 
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class PlainContent extends JSONObject
/*    */ {
/*    */   private static final String TEXT = "text";
/*    */ 
/*    */   public PlainContent(String text)
/*    */   {
/* 10 */     if (text != null)
/* 11 */       put("text", NewsStoryMessage.escapeCharacters(text));
/*    */   }
/*    */ 
/*    */   public PlainContent(JSONObject json)
/*    */   {
/* 16 */     if (json != null)
/*    */       try {
/* 18 */         put("text", json.getString("text"));
/*    */       }
/*    */       catch (NullPointerException e) {
/*    */       }
/*    */   }
/*    */ 
/*    */   public String getText() {
/* 25 */     return NewsStoryMessage.restoreCharacters(getString("text"));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.PlainContent
 * JD-Core Version:    0.6.0
 */