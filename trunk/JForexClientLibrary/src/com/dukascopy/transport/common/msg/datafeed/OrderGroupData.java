/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.PositionStatus;
/*    */ import com.dukascopy.transport.util.Bits;
/*    */ import com.dukascopy.transport.util.Bits.BitsSerializable;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.StreamCorruptedException;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class OrderGroupData
/*    */   implements Bits.BitsSerializable
/*    */ {
/*    */   private static final int VERSION = 1;
/*    */   private static final String HEADER = "Ogd";
/*    */   private String orderGroupId;
/*    */   private String instrument;
/*    */   private PositionStatus status;
/*    */ 
/*    */   public void writeObject(OutputStream os)
/*    */     throws IOException
/*    */   {
/* 28 */     os.write("Ogd".getBytes());
/* 29 */     os.write(1);
/* 30 */     Bits.writeObject(os, this.orderGroupId);
/* 31 */     Bits.writeObject(os, this.instrument);
/* 32 */     Bits.writeObject(os, this.status);
/*    */   }
/*    */ 
/*    */   public void readObject(InputStream is) throws IOException
/*    */   {
/* 37 */     byte[] header = Bits.read(is, new byte["Ogd".length()]);
/* 38 */     if (!Arrays.equals(header, "Ogd".getBytes())) {
/* 39 */       throw new StreamCorruptedException("Deserialization error, unknown header [" + new String(header, "UTF-8") + "]");
/*    */     }
/* 41 */     int version = is.read();
/* 42 */     if (version != 1) {
/* 43 */       throw new StreamCorruptedException("Versions doesn't match, stream version [" + version + "], class version [" + 1 + "]");
/*    */     }
/* 45 */     this.orderGroupId = ((String)Bits.readObject(is, String.class));
/* 46 */     this.instrument = ((String)Bits.readObject(is, String.class));
/* 47 */     this.status = ((PositionStatus)Bits.readObject(is, PositionStatus.class));
/*    */   }
/*    */ 
/*    */   public String getOrderGroupId() {
/* 51 */     return this.orderGroupId;
/*    */   }
/*    */ 
/*    */   public void setOrderGroupId(String orderGroupId) {
/* 55 */     this.orderGroupId = orderGroupId;
/*    */   }
/*    */ 
/*    */   public String getInstrument() {
/* 59 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public void setInstrument(String instrument) {
/* 63 */     this.instrument = instrument;
/*    */   }
/*    */ 
/*    */   public PositionStatus getStatus() {
/* 67 */     return this.status;
/*    */   }
/*    */ 
/*    */   public void setStatus(PositionStatus status) {
/* 71 */     this.status = status;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.OrderGroupData
 * JD-Core Version:    0.6.0
 */