/*     */ package com.dukascopy.transport.common.msg.quote;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class QuoteGroupMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "qGrp";
/*     */   public static final String GROUP_ART_TIME = "artTmst";
/*     */   public static final String GROUP_TIMESTAMP = "tmst";
/*     */   public static final String QUOTES = "qts";
/*     */ 
/*     */   public QuoteGroupMessage()
/*     */   {
/*  28 */     setType("qGrp");
/*     */   }
/*     */ 
/*     */   public QuoteGroupMessage(ProtocolMessage message)
/*     */   {
/*  37 */     super(message);
/*     */ 
/*  39 */     setType("qGrp");
/*  40 */     setGroupArtTime(message.getLong("artTmst"));
/*  41 */     setGroupTimestamp(message.getLong("tmst"));
/*  42 */     setQuotes(message.getJSONArray("qts"));
/*     */   }
/*     */ 
/*     */   public void setGroupArtTime(Long timestamp)
/*     */   {
/*  50 */     if (timestamp != null)
/*  51 */       put("artTmst", timestamp.toString());
/*     */   }
/*     */ 
/*     */   public Long getGroupArtTime()
/*     */   {
/*  60 */     return getLong("artTmst");
/*     */   }
/*     */ 
/*     */   public void setGroupTimestamp(Long timestamp)
/*     */   {
/*  68 */     if (timestamp != null)
/*  69 */       put("tmst", timestamp.toString());
/*     */   }
/*     */ 
/*     */   public Long getGroupTimestamp()
/*     */   {
/*  78 */     return getLong("tmst");
/*     */   }
/*     */ 
/*     */   public void setQuotes(JSONArray quotes)
/*     */   {
/*  86 */     put("qts", quotes);
/*     */   }
/*     */ 
/*     */   public JSONArray getQuotes()
/*     */   {
/*  94 */     return getJSONArray("qts");
/*     */   }
/*     */ 
/*     */   public void setQuoteList(List<QuoteMessage> quotes)
/*     */   {
/* 102 */     JSONArray a = new JSONArray();
/*     */ 
/* 104 */     for (QuoteMessage quote : quotes) {
/* 105 */       a.put(quote);
/*     */     }
/*     */ 
/* 108 */     put("qts", a);
/*     */   }
/*     */ 
/*     */   public List<QuoteMessage> getQuoteList()
/*     */   {
/* 116 */     JSONArray a = getJSONArray("qts");
/* 117 */     List set = new ArrayList(a.length());
/*     */ 
/* 119 */     for (int i = 0; i < a.length(); i++) {
/* 120 */       QuoteMessage quote = (QuoteMessage)ProtocolMessage.parse(a.getString(i));
/* 121 */       if (quote == null) {
/* 122 */         return null;
/*     */       }
/* 124 */       set.add(quote);
/*     */     }
/*     */ 
/* 127 */     return set;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 131 */     QuoteGroupMessage qgm = new QuoteGroupMessage();
/* 132 */     qgm.setGroupTimestamp(Long.valueOf(System.currentTimeMillis()));
/*     */ 
/* 134 */     System.out.println("qgm=" + qgm);
/*     */ 
/* 136 */     QuoteGroupMessage qgmParsed = (QuoteGroupMessage)ProtocolMessage.parse(qgm.toProtocolString());
/*     */ 
/* 138 */     System.out.println("qgmParsed=" + qgmParsed);
/*     */ 
/* 140 */     System.out.println("timestamp=" + qgmParsed.getGroupTimestamp());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.quote.QuoteGroupMessage
 * JD-Core Version:    0.6.0
 */