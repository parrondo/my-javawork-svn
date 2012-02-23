/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.text.ParseException;
/*    */ 
/*    */ public class SetFeedbackFrequencyMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "sff";
/*    */   public static final String FREQUENCY = "freq";
/*    */ 
/*    */   public SetFeedbackFrequencyMessage()
/*    */   {
/* 28 */     setType("sff");
/*    */   }
/*    */ 
/*    */   public SetFeedbackFrequencyMessage(ProtocolMessage message)
/*    */   {
/* 37 */     super(message);
/* 38 */     setType("sff");
/* 39 */     put("freq", message.getInteger("freq"));
/*    */   }
/*    */ 
/*    */   public SetFeedbackFrequencyMessage(String s)
/*    */     throws ParseException
/*    */   {
/* 49 */     super(s);
/* 50 */     setType("sff");
/*    */   }
/*    */ 
/*    */   public SetFeedbackFrequencyMessage(int frequency)
/*    */   {
/* 60 */     setType("sff");
/* 61 */     setFrequency(frequency);
/*    */   }
/*    */ 
/*    */   public Integer getFrequency()
/*    */   {
/* 69 */     return getInteger("freq");
/*    */   }
/*    */ 
/*    */   public void setFrequency(int frequency)
/*    */   {
/* 77 */     put("freq", frequency);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.SetFeedbackFrequencyMessage
 * JD-Core Version:    0.6.0
 */