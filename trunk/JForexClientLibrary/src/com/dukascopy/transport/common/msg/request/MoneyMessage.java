/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class MoneyMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "money";
/*     */   public static final String CURR = "curr";
/*     */   public static final String INSTRUEMNT = "instr";
/*     */   public static final String VALUE = "val";
/*     */ 
/*     */   public MoneyMessage(String s)
/*     */     throws ParseException
/*     */   {
/*  28 */     super(s);
/*  29 */     setType("money");
/*     */   }
/*     */ 
/*     */   public MoneyMessage(JSONObject s) throws ParseException {
/*  33 */     super(s);
/*  34 */     setType("money");
/*     */   }
/*     */ 
/*     */   public MoneyMessage()
/*     */   {
/*  42 */     setType("money");
/*     */   }
/*     */ 
/*     */   public MoneyMessage(String curr, BigDecimal val)
/*     */   {
/*  52 */     setType("money");
/*  53 */     setCurrency(curr);
/*  54 */     setValue(val);
/*     */   }
/*     */ 
/*     */   public MoneyMessage(ProtocolMessage message)
/*     */   {
/*  63 */     super(message);
/*  64 */     put("curr", message.getString("curr"));
/*  65 */     put("val", message.getString("val"));
/*  66 */     put("instr", message.getString("instr"));
/*     */   }
/*     */ 
/*     */   public String getCurrency()
/*     */   {
/*  76 */     return getString("curr");
/*     */   }
/*     */ 
/*     */   public void setCurrency(String currency)
/*     */   {
/*  85 */     put("curr", currency);
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/*  94 */     return getString("instr");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String currency)
/*     */   {
/* 103 */     put("instr", currency);
/*     */   }
/*     */ 
/*     */   public BigDecimal getValue()
/*     */   {
/* 112 */     String val = getString("val");
/* 113 */     if (val == null) {
/* 114 */       return BigDecimal.ZERO;
/*     */     }
/* 116 */     return new BigDecimal(val);
/*     */   }
/*     */ 
/*     */   public void setValue(BigDecimal val)
/*     */   {
/* 126 */     if (val != null)
/* 127 */       put("val", val.toPlainString());
/*     */   }
/*     */ 
/*     */   public Money getMoney()
/*     */   {
/* 132 */     return new Money(getString("val"), getString("curr"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.MoneyMessage
 * JD-Core Version:    0.6.0
 */