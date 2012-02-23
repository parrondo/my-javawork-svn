/*    */ package com.dukascopy.transport.common.protocol.json;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import java.io.DataInputStream;
/*    */ import org.apache.mina.common.ByteBuffer;
/*    */ import org.apache.mina.common.IoSession;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoder;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class JSonProtocolDecoder
/*    */   implements ProtocolDecoder
/*    */ {
/* 16 */   private static final Logger log = LoggerFactory.getLogger(JSonProtocolDecoder.class);
/*    */   private int maxMessageSize;
/*    */ 
/*    */   public JSonProtocolDecoder(int maxMessageSize)
/*    */   {
/* 22 */     this.maxMessageSize = maxMessageSize;
/*    */   }
/*    */ 
/*    */   public void decode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
/* 26 */     String m = "";
/*    */     try {
/* 28 */       IoSessionState sessionState = (IoSessionState)session.getAttribute("sessionState");
/* 29 */       if (sessionState.getState() == 0) {
/* 30 */         sessionState.setState(1);
/*    */       }
/* 32 */       TransportMessageFactory factory = (TransportMessageFactory)session.getAttribute("protoFactory");
/* 33 */       if (factory == null) {
/* 34 */         factory = new TransportMessageFactory(this.maxMessageSize);
/* 35 */         session.setAttribute("protoFactory", factory);
/* 36 */         log.debug("(" + session.getRemoteAddress() + ") message factory created");
/*    */       }
/* 38 */       in.acquire();
/* 39 */       DataInputStream dis = new DataInputStream(in.asInputStream());
/* 40 */       byte[] data = factory.getProtocolBytes(dis, sessionState);
/* 41 */       while (data != null) {
/* 42 */         m = new String(data, "UTF-8");
/* 43 */         ProtocolMessage message = ProtocolMessage.parse(m);
/* 44 */         if (message != null) {
/* 45 */           out.write(message);
/*    */         }
/* 47 */         data = factory.getProtocolBytes(dis, sessionState);
/*    */       }
/* 49 */       dis.close();
/* 50 */       in.release();
/* 51 */       in = null;
/*    */     } catch (Throwable e) {
/* 53 */       e.printStackTrace();
/* 54 */       log.error("(" + session.getRemoteAddress() + ") protocol exception - " + e.getMessage() + ", terminating session");
/* 55 */       log.error("(" + session.getRemoteAddress() + ") protocol exception", e);
/* 56 */       session.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void dispose(IoSession session)
/*    */     throws Exception
/*    */   {
/*    */   }
/*    */ 
/*    */   public void finishDecode(IoSession session, ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.json.JSonProtocolDecoder
 * JD-Core Version:    0.6.0
 */