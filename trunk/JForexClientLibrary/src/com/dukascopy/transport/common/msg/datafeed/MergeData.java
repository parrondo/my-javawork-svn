/*    */ package com.dukascopy.transport.common.msg.datafeed;
/*    */ 
/*    */ import com.dukascopy.transport.util.Bits;
/*    */ import com.dukascopy.transport.util.Bits.BitsSerializable;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.StreamCorruptedException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ 
/*    */ public class MergeData
/*    */   implements Bits.BitsSerializable
/*    */ {
/*    */   private static final int VERSION = 1;
/*    */   private static final String HEADER = "Md";
/*    */   private String newOrderGroupId;
/*    */   private long mergedTime;
/*    */   private List<String> orderGroupIds;
/*    */ 
/*    */   public void writeObject(OutputStream os)
/*    */     throws IOException
/*    */   {
/* 29 */     os.write("Md".getBytes());
/* 30 */     os.write(1);
/* 31 */     Bits.writeObject(os, this.newOrderGroupId);
/* 32 */     os.write(Bits.longBytes(this.mergedTime));
/* 33 */     Bits.writeObject(os, this.orderGroupIds);
/*    */   }
/*    */ 
/*    */   public void readObject(InputStream is) throws IOException
/*    */   {
/* 38 */     byte[] header = Bits.read(is, new byte["Md".length()]);
/* 39 */     if (!Arrays.equals(header, "Md".getBytes())) {
/* 40 */       throw new StreamCorruptedException("Deserialization error, unknown header [" + new String(header, "UTF-8") + "]");
/*    */     }
/* 42 */     int version = is.read();
/* 43 */     if (version != 1) {
/* 44 */       throw new StreamCorruptedException("Versions doesn't match, stream version [" + version + "], class version [" + 1 + "]");
/*    */     }
/* 46 */     this.newOrderGroupId = ((String)Bits.readObject(is, String.class));
/* 47 */     this.mergedTime = Bits.getLong(Bits.read(is, new byte[8]));
/* 48 */     this.orderGroupIds = ((List)Bits.readObject(is, ArrayList.class, String.class));
/*    */   }
/*    */ 
/*    */   public String getNewOrderGroupId() {
/* 52 */     return this.newOrderGroupId;
/*    */   }
/*    */ 
/*    */   public void setNewOrderGroupId(String newOrderGroupId) {
/* 56 */     this.newOrderGroupId = newOrderGroupId;
/*    */   }
/*    */ 
/*    */   public long getMergedTime() {
/* 60 */     return this.mergedTime;
/*    */   }
/*    */ 
/*    */   public void setMergedTime(long mergedTime) {
/* 64 */     this.mergedTime = mergedTime;
/*    */   }
/*    */ 
/*    */   public List<String> getOrderGroupIds() {
/* 68 */     return this.orderGroupIds;
/*    */   }
/*    */ 
/*    */   public void setOrderGroupIds(List<String> orderGroupIds) {
/* 72 */     this.orderGroupIds = orderGroupIds;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.MergeData
 * JD-Core Version:    0.6.0
 */