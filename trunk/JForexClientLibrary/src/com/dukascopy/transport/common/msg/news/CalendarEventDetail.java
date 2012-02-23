/*    */ package com.dukascopy.transport.common.msg.news;
/*    */ 
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class CalendarEventDetail extends JSONObject
/*    */ {
/*    */   private static final String DETAIL_ID = "detail_id";
/*    */   private static final String DESCRIPTION = "description";
/*    */   private static final String ACTUAL = "actual";
/*    */   private static final String DELTA = "delta";
/*    */   private static final String EXPECTED = "expected";
/*    */   private static final String PREVIOUS = "previous";
/*    */ 
/*    */   public CalendarEventDetail()
/*    */   {
/*    */   }
/*    */ 
/*    */   public CalendarEventDetail(JSONObject json)
/*    */   {
/* 24 */     super(json, new String[] { "detail_id", "description", "actual", "delta", "expected", "previous" });
/*    */   }
/*    */ 
/*    */   public String getDetailId() {
/* 28 */     return getString("detail_id");
/*    */   }
/*    */ 
/*    */   public void setDetailId(String id) {
/* 32 */     if (id != null)
/* 33 */       put("detail_id", id);
/*    */   }
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 38 */     return NewsStoryMessage.restoreCharacters(getString("description"));
/*    */   }
/*    */ 
/*    */   public void setDescription(String description) {
/* 42 */     if (description != null)
/* 43 */       put("description", NewsStoryMessage.escapeCharacters(description));
/*    */   }
/*    */ 
/*    */   public String getActual()
/*    */   {
/* 48 */     return getString("actual");
/*    */   }
/*    */ 
/*    */   public void setActual(String actual) {
/* 52 */     if (actual != null)
/* 53 */       put("actual", actual);
/*    */   }
/*    */ 
/*    */   public String getDelta()
/*    */   {
/* 58 */     return getString("delta");
/*    */   }
/*    */ 
/*    */   public void setDelta(String delta) {
/* 62 */     if (delta != null)
/* 63 */       put("delta", delta);
/*    */   }
/*    */ 
/*    */   public String getExpected() {
/* 67 */     return getString("expected");
/*    */   }
/*    */ 
/*    */   public void setExpected(String expected) {
/* 71 */     if (expected != null)
/* 72 */       put("expected", expected);
/*    */   }
/*    */ 
/*    */   public String getPrevious() {
/* 76 */     return getString("previous");
/*    */   }
/*    */ 
/*    */   public void setPrevious(String previous) {
/* 80 */     if (previous != null)
/* 81 */       put("previous", previous);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.news.CalendarEventDetail
 * JD-Core Version:    0.6.0
 */