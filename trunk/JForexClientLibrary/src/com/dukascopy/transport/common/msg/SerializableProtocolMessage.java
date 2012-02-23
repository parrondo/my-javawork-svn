/*    */ package com.dukascopy.transport.common.msg;
/*    */ 
/*    */ import com.dukascopy.transport.common.mina.Base64Encoder;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.io.Serializable;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ 
/*    */ public class SerializableProtocolMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "serializableMessage";
/*    */ 
/*    */   public SerializableProtocolMessage()
/*    */   {
/* 21 */     setType("serializableMessage");
/*    */   }
/*    */ 
/*    */   public SerializableProtocolMessage(ProtocolMessage message)
/*    */   {
/* 28 */     super(message);
/* 29 */     setType("serializableMessage");
/* 30 */     put("data", message.getString("data"));
/*    */   }
/*    */ 
/*    */   public Serializable getData() {
/* 34 */     Serializable res = null;
/* 35 */     String str = getString("data");
/* 36 */     if (str != null) {
/*    */       try {
/* 38 */         byte[] b = Base64Encoder.decode(str);
/* 39 */         ByteArrayInputStream bais = new ByteArrayInputStream(b);
/* 40 */         ObjectInputStream bis = new ObjectInputStream(bais);
/* 41 */         res = (Serializable)bis.readObject();
/*    */       }
/*    */       catch (UnsupportedEncodingException e) {
/* 44 */         e.printStackTrace();
/*    */       }
/*    */       catch (IOException e) {
/* 47 */         e.printStackTrace();
/*    */       }
/*    */       catch (ClassNotFoundException e) {
/* 50 */         e.printStackTrace();
/*    */       }
/*    */     }
/* 53 */     return res;
/*    */   }
/*    */ 
/*    */   public void setData(Serializable data) {
/* 57 */     if (data != null) {
/* 58 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*    */       try
/*    */       {
/* 61 */         ObjectOutputStream oos = new ObjectOutputStream(baos);
/* 62 */         oos.writeObject(data);
/* 63 */         oos.flush();
/* 64 */         byte[] b = baos.toByteArray();
/* 65 */         put("data", new String(Base64Encoder.encode(b)));
/*    */       }
/*    */       catch (IOException e) {
/* 68 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.SerializableProtocolMessage
 * JD-Core Version:    0.6.0
 */