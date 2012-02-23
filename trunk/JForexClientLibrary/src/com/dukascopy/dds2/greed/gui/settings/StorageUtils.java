/*    */ package com.dukascopy.dds2.greed.gui.settings;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ 
/*    */ final class StorageUtils
/*    */ {
/*    */   static byte[] object2bytes(Object object)
/*    */     throws IOException
/*    */   {
/* 17 */     if (object == null) {
/* 18 */       return new byte[0];
/*    */     }
/* 20 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 21 */     ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
/* 22 */     objectOutputStream.writeObject(object);
/* 23 */     return byteArrayOutputStream.toByteArray();
/*    */   }
/*    */ 
/*    */   static Object bytes2Object(byte[] raw) throws IOException, ClassNotFoundException {
/* 27 */     if ((raw == null) || (raw.length == 0)) {
/* 28 */       return null;
/*    */     }
/* 30 */     ByteArrayInputStream bais = new ByteArrayInputStream(raw);
/* 31 */     ObjectInputStream ois = new ObjectInputStream(bais);
/* 32 */     return ois.readObject();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.StorageUtils
 * JD-Core Version:    0.6.0
 */