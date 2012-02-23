/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class FeedCommission
/*     */ {
/*     */   JSONObject obj;
/*     */   public static final String FEED_COMM_PIP = "fcomm";
/*     */   public static final String FEED_COMM_DUKAS = "fcDuk";
/*     */   public static final String FEED_COMM_NONDUKAS = "fcNDuk";
/*     */ 
/*     */   public FeedCommission(BigDecimal commPip, BigDecimal commDukas, BigDecimal commNonDukas)
/*     */   {
/*  20 */     this.obj = new JSONObject();
/*  21 */     setFeedCommission(commPip);
/*  22 */     setFeedCommissionDukas(commDukas);
/*  23 */     setFeedCommissionNondukas(commNonDukas);
/*     */   }
/*     */ 
/*     */   public FeedCommission(JSONObject obj) {
/*  27 */     this.obj = obj;
/*     */   }
/*     */ 
/*     */   public JSONObject getObj() {
/*  31 */     return this.obj;
/*     */   }
/*     */ 
/*     */   public BigDecimal getFeedCommssion()
/*     */   {
/*  40 */     String fcomm = this.obj.getString("fcomm");
/*  41 */     if (fcomm != null) {
/*  42 */       return new BigDecimal(fcomm);
/*     */     }
/*  44 */     return null;
/*     */   }
/*     */ 
/*     */   public void setFeedCommission(BigDecimal fcomm)
/*     */   {
/*  54 */     if (fcomm == null)
/*  55 */       this.obj.put("fcomm", null);
/*     */     else
/*  57 */       this.obj.put("fcomm", fcomm.toPlainString());
/*     */   }
/*     */ 
/*     */   public void setFeedCommissionDukas(BigDecimal fcomm)
/*     */   {
/*  67 */     if (fcomm == null)
/*  68 */       this.obj.put("fcDuk", null);
/*     */     else
/*  70 */       this.obj.put("fcDuk", fcomm.toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getFeedCommssionDukas()
/*     */   {
/*  80 */     String fcomm = this.obj.getString("fcDuk");
/*  81 */     if (fcomm != null) {
/*  82 */       return new BigDecimal(fcomm);
/*     */     }
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   public BigDecimal getFeedCommssionNondukas()
/*     */   {
/*  95 */     String fcomm = this.obj.getString("fcNDuk");
/*  96 */     if (fcomm != null) {
/*  97 */       return new BigDecimal(fcomm);
/*     */     }
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */   public void setFeedCommissionNondukas(BigDecimal fcomm)
/*     */   {
/* 109 */     if (fcomm == null)
/* 110 */       this.obj.put("fcNDuk", null);
/*     */     else
/* 112 */       this.obj.put("fcNDuk", fcomm.toPlainString());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.FeedCommission
 * JD-Core Version:    0.6.0
 */