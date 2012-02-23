/*    */ package com.dukascopy.transport.common.msg.request;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.text.ParseException;
/*    */ 
/*    */ public class SetMarketDepthMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "setMarketDepth";
/*    */   public static final String LEVEL = "level";
/*    */ 
/*    */   public SetMarketDepthMessage()
/*    */   {
/* 26 */     setType("setMarketDepth");
/*    */   }
/*    */ 
/*    */   public SetMarketDepthMessage(ProtocolMessage message)
/*    */   {
/* 35 */     super(message);
/* 36 */     setType("setMarketDepth");
/* 37 */     put("level", message.getInteger("level"));
/*    */   }
/*    */ 
/*    */   public SetMarketDepthMessage(String s)
/*    */     throws ParseException
/*    */   {
/* 47 */     super(s);
/* 48 */     setType("setMarketDepth");
/*    */   }
/*    */ 
/*    */   public SetMarketDepthMessage(int level)
/*    */   {
/* 58 */     setType("setMarketDepth");
/* 59 */     setLevel(level);
/*    */   }
/*    */ 
/*    */   public Integer getLevel()
/*    */   {
/* 67 */     return getInteger("level");
/*    */   }
/*    */ 
/*    */   public void setLevel(int level)
/*    */   {
/* 75 */     put("level", level);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.SetMarketDepthMessage
 * JD-Core Version:    0.6.0
 */