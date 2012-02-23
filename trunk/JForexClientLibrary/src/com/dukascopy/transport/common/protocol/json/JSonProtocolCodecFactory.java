/*    */ package com.dukascopy.transport.common.protocol.json;
/*    */ 
/*    */ import org.apache.mina.filter.codec.ProtocolCodecFactory;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoder;
/*    */ import org.apache.mina.filter.codec.ProtocolEncoder;
/*    */ 
/*    */ public class JSonProtocolCodecFactory
/*    */   implements ProtocolCodecFactory
/*    */ {
/*    */   private int maxMessageSize;
/*    */ 
/*    */   public JSonProtocolCodecFactory(int maxMessageSize)
/*    */   {
/* 13 */     this.maxMessageSize = maxMessageSize;
/*    */   }
/*    */ 
/*    */   public ProtocolDecoder getDecoder() throws Exception {
/* 17 */     return new JSonProtocolDecoder(this.maxMessageSize);
/*    */   }
/*    */ 
/*    */   public ProtocolEncoder getEncoder() throws Exception {
/* 21 */     return new JSonProtocolEncoder();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.json.JSonProtocolCodecFactory
 * JD-Core Version:    0.6.0
 */