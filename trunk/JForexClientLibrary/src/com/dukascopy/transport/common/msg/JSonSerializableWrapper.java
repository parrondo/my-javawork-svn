/*    */ package com.dukascopy.transport.common.msg;
/*    */ 
/*    */ public class JSonSerializableWrapper extends SerializableProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "serialWrapper";
/*    */ 
/*    */   public JSonSerializableWrapper()
/*    */   {
/* 11 */     setType("serialWrapper");
/*    */   }
/*    */ 
/*    */   public JSonSerializableWrapper(ProtocolMessage message)
/*    */   {
/* 18 */     super(message);
/* 19 */     setType("serialWrapper");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.JSonSerializableWrapper
 * JD-Core Version:    0.6.0
 */