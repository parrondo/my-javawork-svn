/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class ReportInfo extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "reportInfo";
/*     */   public static final int EXPORT_PDF = 0;
/*     */   public static final int EXPORT_XLS = 1;
/*     */   public static final int EXPORT_HTML = 2;
/*     */   public static final int EXPORT_CSV = 3;
/*     */ 
/*     */   public ReportInfo()
/*     */   {
/*  31 */     setType("reportInfo");
/*     */   }
/*     */ 
/*     */   public ReportInfo(String name, List<ReportParameter> params)
/*     */   {
/*  39 */     setType("reportInfo");
/*  40 */     setName(name);
/*  41 */     setParams(params);
/*     */   }
/*     */ 
/*     */   public ReportInfo(ProtocolMessage message)
/*     */   {
/*  50 */     super(message);
/*  51 */     setType("reportInfo");
/*  52 */     put("name", message.getString("name"));
/*  53 */     put("params", message.getJSONArray("params"));
/*     */   }
/*     */ 
/*     */   public ReportInfo(JSONObject message)
/*     */     throws ParseException
/*     */   {
/*  62 */     super(message);
/*  63 */     setType("reportInfo");
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  71 */     put("name", name);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  80 */     return getString("name");
/*     */   }
/*     */ 
/*     */   public List<ReportParameter> getParams()
/*     */   {
/*  89 */     JSONArray a = getJSONArray("params");
/*  90 */     List set = new ArrayList(a.length());
/*     */ 
/*  92 */     for (int i = 0; i < a.length(); i++) {
/*  93 */       ReportParameter param = (ReportParameter)ProtocolMessage.parse(a.getString(i));
/*  94 */       if (param == null) {
/*  95 */         return null;
/*     */       }
/*  97 */       set.add(param);
/*     */     }
/*     */ 
/* 100 */     return set;
/*     */   }
/*     */ 
/*     */   public void setParams(List<ReportParameter> params)
/*     */   {
/* 108 */     JSONArray a = new JSONArray();
/*     */ 
/* 110 */     for (ReportParameter param : params) {
/* 111 */       a.put(param);
/*     */     }
/*     */ 
/* 114 */     put("params", a);
/*     */   }
/*     */ 
/*     */   public String getReportParamString() {
/* 118 */     String paramStr = "";
/* 119 */     List l = getParams();
/* 120 */     for (int i = 0; i < l.size(); i++) {
/* 121 */       paramStr = paramStr + "&" + ((ReportParameter)l.get(i)).getParameterString();
/*     */     }
/* 123 */     return paramStr;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ReportInfo
 * JD-Core Version:    0.6.0
 */