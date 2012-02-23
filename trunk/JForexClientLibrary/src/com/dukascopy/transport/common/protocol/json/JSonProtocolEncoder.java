/*    */ package com.dukascopy.transport.common.protocol.json;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.protocol.http.HttpResponse;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import org.apache.mina.common.ByteBuffer;
/*    */ import org.apache.mina.common.IoSession;
/*    */ import org.apache.mina.filter.codec.ProtocolEncoder;
/*    */ import org.apache.mina.filter.codec.ProtocolEncoderOutput;
/*    */ 
/*    */ public class JSonProtocolEncoder
/*    */   implements ProtocolEncoder
/*    */ {
/*    */   public void dispose(IoSession session)
/*    */     throws Exception
/*    */   {
/*    */   }
/*    */ 
/*    */   public void encode(IoSession session, Object messageObject, ProtocolEncoderOutput out)
/*    */     throws Exception
/*    */   {
/*    */     try
/*    */     {
/* 30 */       if (session == null) {
/* 31 */         return;
/*    */       }
/* 33 */       if (session.isClosing()) {
/* 34 */         return;
/*    */       }
/* 36 */       Object mode = session.getAttribute("MODE");
/* 37 */       if ((mode != null) && (mode.equals("HTTP"))) {
/* 38 */         HttpResponse response = (HttpResponse)messageObject;
/* 39 */         ByteBuffer buf = ByteBuffer.wrap(response.toString().getBytes());
/* 40 */         out.write(buf);
/* 41 */         return;
/*    */       }
/* 43 */       String encodedMsg = null;
/* 44 */       if ((messageObject instanceof String)) {
/* 45 */         encodedMsg = (String)messageObject;
/*    */       } else {
/* 47 */         ProtocolMessage msg = (ProtocolMessage)messageObject;
/* 48 */         session.setAttribute("lastEncodedMessage", msg.toProtocolString());
/* 49 */         encodedMsg = msg.toProtocolString();
/*    */       }
/* 51 */       byte[] b = encodedMsg.getBytes("UTF-8");
/* 52 */       ByteArrayOutputStream ar = new ByteArrayOutputStream();
/* 53 */       DataOutputStream s = new DataOutputStream(ar);
/* 54 */       s.writeInt(b.length);
/* 55 */       s.write(b);
/* 56 */       ByteBuffer buf = ByteBuffer.wrap(ar.toByteArray());
/* 57 */       buf.acquire();
/* 58 */       out.write(buf);
/* 59 */       buf.release();
/* 60 */       buf = null;
/*    */     } catch (Exception e) {
/* 62 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.json.JSonProtocolEncoder
 * JD-Core Version:    0.6.0
 */